<template>
  <div>
    <div v-if="generating && !crypto">
      <p>Bezig met het generen van het certificaat! Dit kan even duren, sluit uw browser niet!</p>
    </div>
    <div v-if="!generating && !crypto">
      <div class="form-element">
        <label for="CN">Common name: </label>
        <input data-testid="user-countryName-input" type="text" id="CN"
               v-model="CN"
               :class="{ error: v$.CN.$error }"
               @blur="v$.CN.$touch()"
               placeholder=""
               maxlength="256"/>
      </div>
      <div class="form-element">
        <label for="Land">Land: </label>
        <input data-testid="user-countryName-input" type="text" id="Land"
               v-model="Land"
               :class="{ error: v$.Land.$error }"
               @blur="v$.Land.$touch()"
               placeholder="BE"
               maxlength="5"/>
      </div>
      <div class="form-element">
        <label for="Provincie">Provincie: </label>
        <input data-testid="user-Provincie-input" type="text" id="Provincie"
               v-model="Provincie"
               :class="{ error: v$.Provincie.$error }"
               @blur="v$.Provincie.$touch()"
               placeholder="West-Vlaanderen"
               maxlength="256"/>
      </div>
      <div class="form-element">
        <label for="Stad">Stad: </label>
        <input data-testid="user-Stad-input" type="text" id="Stad"
               v-model="Stad"
               :class="{ error: v$.Stad.$error }"
               @blur="v$.Stad.$touch()"
               placeholder="Koksijde"
               maxlength="256"/>
      </div>
      <div class="form-element">
        <label for="Organisatie">Organisatie: </label>
        <p id="Organisatie">{{org.organisationName}}</p>
      </div>
      <div class="form-element">
        <label for="Organisatieunit">Organisatie unit: </label>
        <input data-testid="user-Organisatieunit-input" type="text" id="Organisatieunit"
               v-model="Organisatieunit"
               :class="{ error: v$.Organisatieunit.$error }"
               @blur="v$.Organisatieunit.$touch()"
               placeholder=""
               maxlength="256"/>
      </div>
      <button @click="generating=true;generate()" class="button-primary-full">
        Generen
      </button>
    </div>
    <div v-if="!generating && crypto && !uploadDone">
      <p>Uw sleutels en certificate request is gegenereerd.
        Hieronder kunt u ze downloaden en om wissellen voor een certificaat</p>
      <button @click="download(0)" class="button-primary-full">
        Private sleutel
      </button>
      <br/>
      <br/>
      <button @click="download(1)" class="button-primary-full">
        Certificate signing request
      </button>
      <br/>
      <br/>
      <p>U staat op punt een certificaat aan te vragen voor volgende aanvraag:</p>
      <p>{{proposal.name}}</p>
      <p>Voor de organisatie {{org.organisationName}}</p>
      <button @click="submit" class="button-primary-full">
        Onderteken
      </button>
    </div>
    <div v-if="!generating && crypto && uploadDone">
      <p>Uw certificaat is gegeneerd, u kunt deze hieronder downloaden </p>
      <button @click="download(3)" class="button-primary-full">
        Certificate signing request
      </button>
    </div>
  </div>
</template>

<script lang=ts>
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import {
  computed, defineComponent, reactive, toRefs,
} from 'vue';

import { useStore } from 'vuex';
import { genCSR } from '@/workers/csr.worker';
import axios from 'axios';

export default defineComponent({
  name: 'CreateCert',
  components: {
  },
  emits: ['cert_form_close'],
  // eslint-disable-next-line no-unused-vars
  setup(props, { emit }) {
    const store = useStore();
    const gottenData = computed(() => store.state.certstore.data);
    const uploadDone = computed(() => store.state.certstore.uploadingDone);
    const proposal = computed(() => store.state.proposalstore.proposal.value);
    const org = computed(() => store.state.certstore.organisation);

    if (proposal.value) {
      // eslint-disable-next-line no-underscore-dangle
      store.dispatch('get_org_cert', proposal.value._links.organisation.href);
    } else console.log('test, no prop');

    const state = reactive({
      serverError: null,
      Land: undefined,
      Provincie: undefined,
      Stad: undefined,
      Organisatieunit: undefined,
      generating: false,
      crypto: undefined,
      organisatie: undefined,
      CN: undefined,
    });

    const rules = reactive({
      Land: { required },
      Provincie: { required },
      Stad: { required },
      Organisatieunit: { required },
      CN: { required },
    });
    const v$ = useVuelidate(rules, state);

    /* generate a csr */
    async function generate() {
      await store.dispatch('reset_upload');
      state.generating = true;
      const values = {
        CN: state.CN,
        Land: state.Land,
        Provincie: state.Provincie,
        Stad: state.Stad,
        Organisatieunit: state.Organisatieunit,
        Organisation: org.value.organisationName,
      };
      state.crypto = await genCSR(values);
      state.generating = false;
    }

    /* sign a certificate */
    function submit() {
      if (!v$.$invalid) {
        const blob = new Blob([state.crypto[2]], { type: 'application/pkcs10' });
        const file = new FormData();
        file.append('file', blob, 'csr.csr');
        store.dispatch('create_cert', {
          file,
          cert: {
            proposalId: proposal.value.id,
            file: undefined,
          },
        }).catch((error) => {
          state.serverError = error;
          console.error(error);
        });
      }
    }

    /* Download a string as a file */
    async function download(key) {
      let name; let blob;
      if (key === 0) {
        // private
        blob = new Blob([state.crypto[0]], { type: 'application/pkcs8' });
        name = 'private.key';
      } else if (key === 3) {
        if (gottenData.value) {
          const data = await axios.get(gottenData.value.file.fileLocation,
            { responseType: 'blob' });
          blob = new Blob([data.data], { type: 'application/x-x509-user-cert' });

          name = `${org.value.name}.crt`;
        }
      } else {
        // csr
        blob = new Blob([state.crypto[2]], { type: 'application/pkcs10' });
        name = 'certificate request.csr';
      }
      const link = document.createElement('a');
      link.href = URL.createObjectURL(blob);
      link.download = name;
      link.click();
      URL.revokeObjectURL(link.href);
    }

    return {
      ...toRefs(state),
      v$,
      submit,
      gottenData,
      uploadDone,
      generate,
      download,
      org,
      proposal,
    };
  },
});
</script>

<style scoped lang="sass">
.error
  border: 2px solid red
</style>
