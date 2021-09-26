<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form">
    <InputField label="Naam:*"
                type="text"
                v-model="name"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                dataTestid="permission-name-input">
      <span v-if="v$.name.required.$invalid">
        Naam ontbreekt
      </span>
    </InputField>
    <InputField label="Beschrijving:*"
                type="text"
                v-model="description"
                maxlength="4096"
                :alwaysShowError="showAllErrors"
                dataTestid="permission-description-input">
      <span v-if="v$.description.required.$invalid">
        Beschrijving ontbreekt
      </span>
    </InputField>
    <InputField label="Code:*"
                type="text"
                v-model="code"
                :alwaysShowError="showAllErrors"
                dataTestid="permission-code-input">
      <span v-if="v$.code.required.$invalid">
        Code ontbreekt
      </span>
    </InputField>
    <div v-if="!giveProofByLink">
      <input type="file" id="processing" @change="chooseFile($event)">
      <button @click.prevent="uploadFile">Upload bestand</button>
    </div>
    <br/>
    <button :class="{ 'button-primary-full-disabled': v$.$invalid }"
            type="submit"
            class="button-primary-full floatRight">Indienen</button>
  </form>
</template>

<script lang=ts>
import useVuelidate from '@vuelidate/core';
import { required } from '@vuelidate/validators';
import {
  reactive,
  toRefs,
  defineComponent,
  ref,
} from 'vue';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';

export default defineComponent({
  props: {
    permission: Object,
    organisationId: null,
  },
  components: {
    InputField,
  },
  emits: ['permission_form_close'],
  setup(props, { emit }) {
    const state = reactive({
      name: props.permission ? props.permission.name : '',
      code: props.permission ? props.permission.code : '',
      description: props.permission ? props.permission.description : '',
      link: props.permission ? props.permission.link : '',
      proof: props.permission ? props.permission.proof : '',
      file: '',
    });

    const rules = {
      name: { required },
      code: { required },
      description: { required },
      // link: { required },
      // proof: { required },
    };

    const showAllErrors = ref(false);

    const v$ = useVuelidate(rules, state);

    const store = useStore();

    function submit(invalid) {
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      // if this.permission => its an update, else create new permission
      if (props.permission) {
        // merge props for id and _links for updated permission
        store.dispatch('update_permission', { ...props.permission, ...state });
      } else {
        store.dispatch('create_permission', { ...state, organisationId: props.organisationId });
      }
      emit('permission_form_close');
    }

    const chooseFile = (event) => {
      // eslint-disable-next-line prefer-destructuring
      state.file = event.target.files[0];
    };

    const uploadFile = () => {
      const formData = new FormData();
      formData.append('file', state.file);
      // eslint-disable-next-line no-return-assign
      store.dispatch('upload_permission_file', formData).then(({ data }) => state.proof = data.id);
    };

    const giveProofByLink = ref(false);

    return {
      ...toRefs(state),
      v$,
      submit,
      showAllErrors,
      giveProofByLink,
      chooseFile,
      uploadFile,
    };
  },
  methods: {
  },
});
</script>

<style scoped lang="sass">
.error
  border: 2px solid red
</style>
