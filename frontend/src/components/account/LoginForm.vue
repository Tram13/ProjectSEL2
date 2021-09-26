<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form">
    <InputField label="Email:*"
                type="email"
                v-model="email"
                placeholder="jan.janssens@gmail.com"
                :allwaysShowError="showAllErrors"
                dataTestid="user-email-input">
      <span v-if="v$.email.required.$invalid">
        Email ontbreekt
      </span>
    </InputField>
    <InputField label="Wachtwoord:*"
                type="password"
                v-model="password"
                maxlength="256"
                :allwaysShowError="showAllErrors"
                dataTestid="user-password-input">
      <span v-if="v$.password.required.$invalid">
        Wachtwoord ontbreekt
      </span>
    </InputField>
    <button data-testid="login-button"
            type="submit"
            class="button-primary-full"
            :class="{ 'button-primary-full-disabled': v$.$invalid }">
            Aanmelden
    </button>
    <router-link data-testid="register-button"
                 :to="{name: 'Registration'}"
                 class="button-ternary-full">
        <button>Registreren</button>
    </router-link>
    <div v-if="errorCode === 400" class="errorMsg-group">
      <div class="errorMsg">Incorrect email of wachtwoord</div>
    </div>
  </form>
</template>

<script lang="ts">
import {
  reactive,
  toRefs,
  defineComponent,
  ref,
} from 'vue';
import useVuelidate from '@vuelidate/core';
import { required, email } from '@vuelidate/validators';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';

export default defineComponent({
  name: 'LoginForm',
  components: {
    InputField,
  },
  setup() {
    const state = reactive({
      email: '',
      password: '',
      errorCode: '',
    });

    const rules = {
      email: { required, email },
      password: { required },
    };

    const showAllErrors = ref(false);

    const v$ = useVuelidate(rules, state);
    const store = useStore();

    function submit(invalid) {
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      store.dispatch('login', { email: state.email, password: state.password })
        .then(() => this.$router.push({ name: 'Home' }))
        .catch((error) => {
          state.errorCode = error.response.status;
        });
    }

    return {
      showAllErrors,
      ...toRefs(state),
      v$,
      submit,
    };
  },
});
</script>
<style scoped lang="sass">
  .errorMsg-group
    box-shadow: 0 0 12px 1px #ccc
</style>
