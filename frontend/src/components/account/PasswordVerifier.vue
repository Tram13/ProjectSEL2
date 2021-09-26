<template>
  <form @submit.prevent="submit" class="input-form popup_window_content">
    <InputField label="Wachtwoord:*"
                type="password"
                v-model="password"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                dataTestid="user-password-input">
      <span v-if="v$.password.required.$invalid">
        Wachtwoord ontbreekt
      </span>
    </InputField>
    <button data-testid="login-button" :disabled="v$.$invalid" type="submit">Bevestigen</button>
    <div v-if="errorCode === 400" class="errorMsg-group">
      <div class="errorMsg">incorrect wachtwoord</div>
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
import { required } from '@vuelidate/validators';
import { useStore } from 'vuex';
import InputField from '@/components/elements/base/InputField.vue';

export default defineComponent({
  name: 'PasswordVerifier',
  emits: ['correct-password', 'incorrect-password'],
  components: {
    InputField,
  },
  setup(props, { emit }) {
    const state = reactive({
      password: '',
      errorCode: '',
    });
    const rules = {
      password: { required },
    };
    const showAllErrors = ref(false);
    const v$ = useVuelidate(rules, state);
    const store = useStore();
    function submit() {
      if (v$.value.password.$invalid) {
        showAllErrors.value = true;
        return;
      }
      store.dispatch('verifyPassword', state.password)
        .then(() => emit('correct-password'))
        .catch((error) => {
          if (error.response) {
            state.errorCode = error.response.status;
            if (state.serverError === 'Unauthorized') {
              emit('incorrect-password');
            }
          } else {
            console.error(error);
          }
        });
    }
    return {
      ...toRefs(state),
      v$,
      submit,
      showAllErrors,
    };
  },
});
</script>
<style scoped lang="sass">
  .errorMsg-group
    box-shadow: 0 0 12px 1px #ccc
    width: 90%
  .popup_window
    position: fixed
    margin: 0 auto 0 -20%
    top: 35%
    width: 40%
    left: 50%
</style>
