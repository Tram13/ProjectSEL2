import { createApp } from 'vue';
import axios from 'axios';
import App from './App.vue';
import router from './router';
import store from './store';

// add axios interceptor
axios.interceptors.response.use(
  (response) => response,
  (error) => {
    // if not authenticated
    if (error && error.message !== 'abort' && error.response && error.response.status === 403) {
      // try to refresh token
      store.dispatch('refreshToken')
        // if this fails token is not available or is already used
        .catch(() => {
          // logout to clear localstorage
          store.dispatch('logout');
          // clear all state in store by reloading the page
          location.reload(); // eslint-disable-line
        });
    }
    return Promise.reject(error);
  },
);

// Get the endpoints from the server
store.dispatch('get_endpoints')
  .then(() => {
    // when succesfull check if user is logged in, if so
    // get credentials
    const credentialsString = localStorage.getItem('credentials');
    if (credentialsString) {
      // if exists parse credentials and user
      const credentials = JSON.parse(credentialsString);
      // set the credentials and user in store
      store.commit('SET_SESSION_DATA', credentials);
      // get the userdata
      store.dispatch('get_account_information', credentials);
    } else {
      // else, redirect user to login
      router.replace({ name: 'Login' });
    }
  })
  .catch((error) => {
    console.log(error);
    return router.replace({ name: 'error' });
  })
  .finally(() => createApp(App).use(store).use(router).mount('#app'));
