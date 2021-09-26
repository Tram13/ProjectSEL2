<template>
  <form @submit.prevent="submit(v$.$invalid)" class="input-form" autocomplete="off">

    <InputField label="Naam:*"
                type="text"
                v-model="lastName"
                placeholder="Janssens"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                data-Testid="user-lastName-input">
      <span v-if="v$.lastName.required.$invalid">
        achternaam ontbreekt
      </span>
    </InputField>
    <InputField label="Voornaam:*"
                type="text"
                v-model="firstName"
                placeholder="Jan"
                maxlength="256"
                :alwaysShowError="showAllErrors"
                data-Testid="user-firstName-input">
      <div v-if="v$.firstName.required.$invalid">
        voornaam ontbreekt
      </div>
    </InputField>
    <InputField label="Email:*"
                type="email"
                v-model="email"
                placeholder="jan.janssens@gmail.com"
                :alwaysShowError="showAllErrors"
                data-Testid="user-email-input"
                @change="inputTyped">
      <div v-if="v$.email.required.$invalid">
        email ontbreekt
      </div>
      <div v-else-if="v$.email.email.$invalid">
        ongeldige email
      </div>
      <div v-else-if="serverError === 409">
        er bestaat al een gebruiker met dit e-mailadres
      </div>
    </InputField>

    <template v-if="isAdmin">
    <label data-Testid="user-role-input">Rol:*</label>
    <br/>
    <input type="radio" v-model="role" value="customer" name="group1" id="r1">
    <label for="r1">Gebruiker</label>
    <input type="radio" v-model="role" value="employee" name="group1" id="r2">
    <label for="r2">Medewerker</label>
    <input type="radio" v-model="role" value="admin" name="group1" id="r3">
    <label for="r3">Admin</label>
    <br/>
    <br/>
    </template>

    <InputField v-if="canEditPassword"
                :label="`Wachtwoord:${editing? '' : '*'}`"
                type="password"
                v-model="password"
                :placeholder="editing ? 'Laat leeg om wachtwoord te behouden' : 'Jouw wachtwoord'"
                :autocomplete="false"
                :alwaysShowError="showAllErrors"
                data-Testid="user-password-input">
      <div v-if="v$.password.required.$invalid">
        wachtwoord ontbreekt
      </div>
      <template v-else-if="v$.password.$invalid">
        <div v-if="v$.password.minLength.$invalid">
          wachtwoord moet minstens 8 tekens bevatten
        </div>
        <div v-if="v$.password.containsDigit.$invalid">
          wachtwoord moet minstens één cijfer bevatten
        </div>
        <div v-if="v$.password.containsLowerCase.$invalid">
          wachtwoord moet minstens één kleine letter bevatten
        </div>
        <div v-if="v$.password.containsUpperCase.$invalid">
          wachtwoord moet minstens één hoofdletter bevatten
        </div>
      </template>
    </InputField>

    <InputField v-if="canEditPassword"
                :label="`Wachtwoord bevestigen:${editing? '' : '*'}`"
                type="password"
                v-model="passwordValidation"
                placeholder="Herhaal je wachtwoord"
                maxlength="256"
                :autocomplete="false"
                :alwaysShowError="showAllErrors"
                data-Testid="user-password-confirm-input">
      <div v-if="v$.passwordValidation.sameAs.$invalid">
        wachtwoord is niet bevestigd
      </div>
    </InputField>

    <button data-Testid="submit-button"
            type="submit"
            class="button-primary-full floatRight"
            :class="{ 'button-primary-full-disabled': v$.$invalid }">
      {{ editing ? 'Aanpassen' : 'Aanmaken' }}
    </button>

  </form>
  <Popup v-if="showPasswordVerifier" @closePopup="showPasswordVerifier = false">
    <PasswordVerifier @correct-password="updateUser"/>
  </Popup>
</template>

<script lang=ts>
import useVuelidate from '@vuelidate/core';
import {
  required,
  email,
  sameAs,
  helpers,
} from '@vuelidate/validators';
import {
  reactive,
  toRefs,
  defineComponent,
  computed,
  ref,
} from 'vue';
import { useStore } from 'vuex';
import PasswordVerifier from '@/components/account/PasswordVerifier.vue';
import Popup from '@/components/elements/popup/popup.vue';
import InputField from '@/components/elements/base/InputField.vue';

const containsDigit = (value) => /\d/.test(value);
const containsLowerCase = (value) => /[a-z]/.test(value);
const containsUpperCase = (value) => /[A-Z]/.test(value);

export default defineComponent({
  name: 'CreateUser',
  components: {
    PasswordVerifier,
    Popup,
    InputField,
  },
  props: {
    user: { type: Object, default: null },
    registration: Boolean,
  },
  emits: ['user_form_close'],
  setup(props, { emit }) {
    const editing = props.user !== null;
    const store = useStore();
    const authUser = computed(() => store.state.authenticationstore.user);
    const showPasswordVerifier = ref(false);
    const state = reactive({
      lastName: props.user ? props.user.lastName : '',
      firstName: props.user ? props.user.firstName : '',
      email: props.user ? props.user.email : '',
      role: props.user ? props.user.role : 'customer',
      serverError: null,
    });

    function handleServerError(err) {
      if (err && err.response) {
        state.serverError = err.response.status;
      }
      console.error(err);
    }

    const showAllErrors = ref(false);
    // only an admin may set roles
    const isAdmin = authUser.value && authUser.value.role === 'admin';
    // only the owner of the account may change the password
    const canEditPassword = !props.user || props.user.id === authUser.value.id;

    let rules;

    function inputTyped() {
      state.serverError = null;
      showAllErrors.value = false;
    }

    if (canEditPassword) {
      // eslint-disable-next-line
      rules = computed(() => {
        return {
          lastName: { required },
          firstName: { required },
          email: { required, email },
          role: { required },

          password: {
            required: (v) => (editing && !helpers.req(v)) || helpers.req(v),
            minLength: (v) => (editing && !helpers.req(v)) || helpers.len(v) >= 8,
            containsDigit: (v) => (editing && !helpers.req(v)) || containsDigit(v),
            containsLowerCase: (v) => (editing && !helpers.req(v)) || containsLowerCase(v),
            containsUpperCase: (v) => (editing && !helpers.req(v)) || containsUpperCase(v),
          },
          passwordValidation: {
            sameAs: sameAs(state.password),
          },
        };
      });
      state.password = '';
      state.passwordValidation = '';
    } else {
      // eslint-disable-next-line
      rules = computed(() => {
        return {
          lastName: { required },
          firstName: { required },
          email: { required, email },
          role: { required },
        };
      });
    }

    const v$ = useVuelidate(rules, state);
    function updateUser() {
      // merge old user for id and _links with new fields
      const user = { ...props.user, ...state };
      if (!isAdmin) {
        delete user.role; // otherwise error 403: forbidden
      }
      if (!state.password) {
        delete user.password;
      }
      store.dispatch('update_user', user)
        .catch(handleServerError)
        .finally(() => {
          if (showPasswordVerifier.value && !state.serverError) {
            showPasswordVerifier.value = false;
            emit('user_form_close');
          }
        });
    }

    function submit(invalid) {
      // make all fields dirty to check if everything is correct
      // v$.$touch(); // eslint-disable-line
      if (invalid) {
        showAllErrors.value = true;
        return;
      }
      if (state.password === '') {
        delete state.password;
      }
      if (!v$.$invalid) {
        // if props.user => its an update, else create new user
        if (props.user) {
          if (state.password) {
            // promp user to verify password
            showPasswordVerifier.value = true;
            return;
          }
          updateUser();
        } else if (props.registration) {
          store.dispatch('register', state)
            .then(() => this.$router.push({ name: 'Login' }))
            .catch(handleServerError)
            .finally(() => {
              if (!state.serverError) {
                emit('user_form_close');
              }
            });
        } else {
          store.dispatch('create_user', state)
            .catch(handleServerError)
            .finally(() => {
              if (!state.serverError) {
                emit('user_form_close');
              }
            });
        }
      }
    }

    return {
      editing,
      showAllErrors,
      ...toRefs(state),
      v$,
      submit,
      isAdmin,
      canEditPassword,
      showPasswordVerifier,
      updateUser,
      inputTyped,
    };
  },
});
</script>

<style scoped lang="sass">
  .error
    border: 2px solid red
</style>
