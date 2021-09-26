/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');
const jwt = require('jsonwebtoken');

export const state = {
  sessionToken: null,
  refreshToken: null,
  user: null,
  organisations: [],
  selectedOrganisation: null,
};

export const mutations = {
  SET_SESSION_DATA(state, credentials) {
    // set tokens
    state.sessionToken = credentials.sessionToken;
    state.refreshToken = credentials.refreshToken;
    // set tokens in localstorage
    localStorage.setItem('credentials', JSON.stringify(credentials));
    // set axios header
    axios.defaults.headers.common.Authorization = `Bearer ${credentials.sessionToken}`;
  },
  REFRESH_SESSION_DATA(state, { sessionToken }) {
    // remove old localstorage item
    localStorage.removeItem('credentials');
    // set new session token, refresh is the same and wont work next time
    // -> user will be redirected to login ( axios interceptor )
    state.sessionToken = sessionToken;
    // set new localstorage item ( with refreshToken for consistency )
    localStorage.setItem('credentials', JSON.stringify({
      sessionToken,
      refreshToken: state.refreshToken,
    }));
    // set the new token in axios
    axios.defaults.headers.common.Authorization = `Bearer ${sessionToken}`;
  },
  LOGOUT(state) {
    // nullify the loginstate
    state.sessionToken = null;
    state.refreshToken = null;
    state.user = null;
    state.organisations = [];
    state.selectedOrganisation = null;
    // remove credentials from localStorage
    localStorage.removeItem('credentials');

    // remove role from localStorage
    localStorage.removeItem('role');
    // remove the axios header

    delete axios.defaults.headers.common.Authorization;
  },
  SET_AUTH_USER(state, user) {
    // set the auth user, api returns _embedded.userList else just take user
    if (user._embedded) {
      [state.user] = user._embedded.userList;
    } else {
      state.user = user;
    }
    // set role in localStorage for conditional routing
    localStorage.setItem('role', state.user.role);
  },
  UPDATE_USER(state, user) {
    // if user is updated in an inspectpage it is possible that the user was the loggedin
    // user => auth user needs to update if ids are equal
    if (state.user.id === user.id) {
      // set updated user
      state.user = user;
    }
  },
  DELETE_USER(state, user) {
    // if user is updated in an inspectpage it is possible that the user was the logged
    // user => auth user needs to update if ids are equal
    if (state.user.id === user.id) {
      state.sessionToken = null;
      state.refreshToken = null;
      state.user = null;
      localStorage.removeItem('credentials');
    }
  },
  ADD_ACCOUNT_ORGANISATION(state, organisation) {
    // add organisation to organisations
    state.organisations.push(organisation);
  },
  ADD_USER_ORGANISATION(state, { user, organisation }) {
    if (organisation.accepted) {
      if (state.user.id === user.id) {
        const index = state.organisations.findIndex(
          (_) => _.organisation.id === organisation.organisation.id,
        );
        if (index === -1) {
          // if organisation does not exist add it
          state.organisations.push(organisation);
        } else {
          // else update the organisation
          state.organisations[index] = organisation;
        }
        // if user has no organisations set one
        if (state.organisations.length === 1) { // length === 1 because just added one
          this.commit('SET_SELECTED_ORGANISATION', organisation); // not very vuex but a nice solution
        }
      }
    }
  },
  DELETE_USER_ORGANISATION(state, { user, organisation }) {
    if (state.user.id === user.id) {
      state.organisations = state.organisations.filter(
        (_) => _.organisation.id !== organisation.id,
      );
      // if selectedOrganisation is deleted set a new one
      if (state.selectedOrganisation.organisation.id === organisation.id) {
        this.commit('SET_SELECTED_ORGANISATION', null); // not very vuex but a nice solution
      }
    }
  },
  UPDATE_ORGANISATION(state, organisation) {
    state.organisations.forEach((_, i) => {
      if (_.organisation.id === organisation.id) {
        state.organisations[i].organisation = organisation;
      }
    });
  },
  DELETE_ORGANISATION(state, organisation) {
    state.organisations = state.organisations.filter(
      (_) => _.organisation.id !== organisation.id,
    );
    // only if the currently selected organisation is deleted
    if (state.selectedOrganisation.organisation.id === organisation.id) {
      this.commit('SET_SELECTED_ORGANISATION', null); // not very vuex but a nice solution
    }
  },
  SET_SELECTED_ORGANISATION(state, organisationId) {
    // get the organisation
    const organisation = state.organisations.find((_) => _.organisation.id === organisationId);
    // if organisation is in organisations
    if (organisation) {
      state.selectedOrganisation = organisation;
      // set selectedOrganisation in localstorage
      localStorage.setItem('selectedOrganisation', JSON.stringify(organisationId));
    } else if (state.organisations) {
      // if organisation is not found take the first out of the list
      state.selectedOrganisation = state.organisations.find((_) => _.organisation.approved);
      // set selectedOrganisation in localstorage
      localStorage.setItem('selectedOrganisation', JSON.stringify(organisationId));
    } else {
      // else the user has no organisations,
      // make sure there is no selectedOrganisation in the localstorage
      localStorage.removeItem('selectedOrganisation');
    }
  },
};

export const actions = {
  register({ rootGetters }, user) {
    return axios.post(`${rootGetters.endpoint('auth')}/register`, encodeObject(user, encodedFields.user));
  },
  login({ commit, rootGetters, dispatch }, credentials) {
    delete axios.defaults.headers.common.Authorization;
    return axios.post(`${rootGetters.endpoint('auth')}/login`, credentials)
      .then(({ data }) => {
        commit('SET_SESSION_DATA', data);
        return dispatch('get_account_information', data);
      });
  },
  refreshToken({ commit, rootGetters }) {
    return axios.post(`${rootGetters.endpoint('auth')}/refreshSessionToken`, {
      refreshToken: state.refreshToken,
    })
      .then(({ data }) => commit('REFRESH_SESSION_DATA', data));
  },
  logout({ commit }) {
    // remove the state
    commit('LOGOUT');
  },
  verifyPassword({ rootGetters }, password) {
    // return the request so errors can be caught
    return axios.post(`${rootGetters.endpoint('auth')}/login`, {
      email: state.user.email,
      password,
    });
  },
  get_account_information({ commit, dispatch, rootGetters }, { sessionToken }) {
    // decode the jwt token
    const decoded = jwt.decode(sessionToken, 'RS256');
    // decoded.sub contains the userid
    return axios.get(`${rootGetters.endpoint('users')}/${decoded.sub}`)
      .then(({ data }) => commit('SET_AUTH_USER', decodeObject(data, encodedFields.user)))
      .then(() => dispatch('get_account_organisations'));
  },
  get_account_organisations({ commit }) {
    // get all organisations from the user
    axios.get(state.user._links.organisations.href, {
      params: {
        accepted: true,
      },
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.organisationList.forEach(
            // add the accounts
            (organisation) => commit('ADD_ACCOUNT_ORGANISATION', organisation),
          );
        }
      })
      .then(() => {
        // set selected organisation, if id not in localstorage, SET_SELECTED_ORGANISATION
        // will set the first organisation in the list
        const organisationString = localStorage.getItem('selectedOrganisation');
        if (organisationString) {
          // remove the localstorage entry, will be set in mutation
          localStorage.removeItem('selectedOrganisation');
          // set selectedOrganisation
          commit('SET_SELECTED_ORGANISATION', JSON.parse(organisationString));
        } else {
          // set selectedOrganisation without id => first in organisations list
          commit('SET_SELECTED_ORGANISATION', null);
        }
      })
      .catch(console.error);
  },
};

export const getters = {
  loggedIn(state) {
    return !!state.sessionToken;
  },
  role(state) {
    if (state.user) {
      return state.user.role;
    }
    return null;
  },
};
