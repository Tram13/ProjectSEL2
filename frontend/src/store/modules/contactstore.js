/* eslint no-shadow: ["error", { "allow": ["state"] }] */
/* eslint no-underscore-dangle: 0 */
/* eslint no-param-reassign: 0 */
import { encodeObject, decodeObject, encodedFields } from '@/helpers/htmlEntities';

const axios = require('axios');

export const state = {
  contact: { value: null, loading: false, error: undefined },
  contacts: { value: [], loading: false, error: undefined },
};

export const mutations = {
  SET_CONTACT(state, contact) {
    state.contact.value = contact;
  },
  ADD_CONTACT(state, contact) {
    const index = state.contacts.value.findIndex((_) => _.id === contact.id);
    if (index === -1) {
      // if contact does not exist add it
      state.contacts.value.push(contact);
    } else {
      // else update the contact
      state.contacts.value[index] = contact;
    }
  },
  DELETE_CONTACT(state, contact) {
    state.contacts.value = state.contacts.value.filter((cont) => cont.id !== contact.id);
  },
  UPDATE_CONTACT(state, contact) {
    state.contact.value = contact;
    state.contacts.value.forEach((cont, i) => {
      if (cont.id === contact.id) state.contacts.value[i] = contact;
    });
  },
  CLEAR_CONTACTS(state) {
    state.contacts.value = [];
  },
  TOGGLE_LOADING_CONTACT(state) {
    state.contact.loading = !state.contact.loading;
  },
  TOGGLE_LOADING_CONTACTS(state) {
    state.contacts.loading = !state.contacts.loading;
  },
  CLEAR_CONTACT(state) {
    state.contact.value = [];
    state.contact.error = undefined;
  },
};

export const actions = {
  fetch_contact({ commit, rootGetters }, { organisationId, contactId }) {
    commit('TOGGLE_LOADING_CONTACT');
    commit('CLEAR_CONTACT');
    axios.get(`${rootGetters.endpoint('organisations')}/${organisationId}/contacts/${contactId}`)
      .then(({ data }) => commit('SET_CONTACT', decodeObject(data, encodedFields.contact)))
      .catch(console.error)
      .finally(() => commit('TOGGLE_LOADING_CONTACT'));
  },
  fetch_contacts({ commit, rootGetters }, organisationId) {
    commit('TOGGLE_LOADING_CONTACTS');
    commit('CLEAR_CONTACTS');
    return axios.get(`${rootGetters.endpoint('organisations')}/${organisationId}/contacts`)
      .then((response) => response.data)
      .then((data) => {
        if (data._embedded) {
          data._embedded.contactList.forEach(
            (contact) => commit('ADD_CONTACT', decodeObject(contact, encodedFields.contact)),
          );
        }
      })
      .catch((error) => console.error(error))
      .finally(commit('TOGGLE_LOADING_CONTACTS'));
  },
  fetch_contacts_pagination({ commit, rootGetters }, params) {
    commit('TOGGLE_LOADING_CONTACTS');
    commit('CLEAR_CONTACTS');
    const { organisationId } = params;
    delete params.organisationId;
    return axios.get(`${rootGetters.endpoint('organisations')}/${organisationId}/contacts`, {
      params: {
        ...params,
      },
    })
      .then(({ data }) => {
        if (data._embedded) {
          data._embedded.contactList.forEach(
            (contact) => commit('ADD_CONTACT', decodeObject(contact, encodedFields.contact)),
          );
        }
        commit('TOGGLE_LOADING_CONTACTS');
        return data.total;
      })
      .catch(console.error);
  },
  create_contact({ commit, rootGetters }, { contact, organisationId }) {
    encodeObject(contact, encodedFields.contact);
    return axios.post(`${rootGetters.endpoint('organisations')}/${organisationId}/contacts`, contact)
      .then(({ data }) => commit('SET_CONTACT', decodeObject(data, encodedFields.contact)));
  },
  update_contact({ commit }, contact) {
    encodeObject(contact, encodedFields.contact);
    return axios.patch(contact._links.self.href, contact)
      .then(() => commit('UPDATE_CONTACT', decodeObject(contact, encodedFields.contact)))
      .catch(console.error);
  },
  delete_contact({ commit }, contact) {
    axios.delete(contact._links.self.href)
      .then(() => commit('DELETE_CONTACT', contact))
      .catch(console.error);
  },
};
