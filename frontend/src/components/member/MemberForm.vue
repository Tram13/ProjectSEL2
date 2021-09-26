<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form" autocomplete="off">
    <InputField label="Email:*"
                type="text"
                v-model="email"
                placeholder="jan.janssens@gmail.com"
                :alwaysShowError="showAllErrors"
                maxlenth="256"
                @change="inputTyped">
      <div v-if="v$.email.required.$invalid">
        Email ontbreekt
      </div>
      <div v-if="v$.email.email.$invalid">
        Ongeldige email
      </div>
      <div v-if="errorCode === 404">
        Er is geen user geregistreerd met dit email
      </div>
      <div v-if="errorCode === 409">
        Deze user is al uitgenodigd voor deze organisatie
      </div>
    </InputField>
    <label>Rol:*</label>
    <br/>
    <input type="radio" v-model="role" checked="checked" value="member" name="group1" id="r1">
    <label for="r1">Lid</label>
    <input type="radio" v-model="role" value="manager" name="group1" id="r2">
    <label for="r2">Manager</label>
    <br/>
    <br/>
    <button class="button-primary-full floatRight"
            type="submit" :class="{ 'button-primary-full-disabled': v$.$invalid }">
      Uitnodiging versturen
    </button>
  </form>
</template>

<script lang=ts>
import InputField from '@/components/elements/base/InputField.vue';
import {
  computed, defineComponent, reactive, ref, toRefs,
} from 'vue';
import { useStore } from 'vuex';
import { email, required } from '@vuelidate/validators';
import useVuelidate from '@vuelidate/core';

export default defineComponent({
  name: 'CreateMember',
  components: {
    InputField,
  },
  props: {
    user: { type: Object, default: null },
    registration: Boolean,
    organisationId: null,
  },
  emits: ['member_form_close'],
  setup(props, { emit }) {
    const editing = props.user !== null;
    const store = useStore();
    const authUser = computed(() => store.state.authenticationstore.user);
    const state = reactive({
      email: props.user ? props.user.email : '',
      role: props.user ? props.user.role : 'member',
      organisationId: props.organisationId,
      errorCode: null,
    });

    const showAllErrors = ref(false);

    const rules = computed(() => ({
      email: { required, email },
      role: { required },
    }));

    const v$ = useVuelidate(rules, state);

    function submit(invalid) {
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      store.dispatch('add_user_with_email_to_organisation', {
        email: state.email,
        organisationId: state.organisationId,
        role: state.role,
      }).then(() => emit('member_form_close'))
        .catch((error) => {
          if (error.response) {
            showAllErrors.value = true;
            state.errorCode = error.response.status;
          } else {
            console.error(error);
          }
        });
    }

    function inputTyped() {
      state.errorCode = null;
      showAllErrors.value = false;
    }

    return {
      editing,
      authUser,
      state,
      showAllErrors,
      ...toRefs(state),
      v$,
      submit,
      inputTyped,
    };
  },
});
</script>

<style scoped>
  .error {
    border: 2px solid red;
  }
</style>
