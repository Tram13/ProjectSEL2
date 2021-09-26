<template>
  <div class="content-container">
    <div class='banner'>
      <div class='banner-content'>
        <div class="title-bar">
          <h1>Gebruiker</h1>
          <button class="button-secondary-edge right"
                  @click="showNewUserPopup = true">
            Gebruiker Aanmaken
          </button>
          <Popup size="medium" v-if="showNewUserPopup" @closePopup="showNewUserPopup = false">
            <h1>Gebruiker aanmaken</h1>
            <UserForm @user_form_close="showNewUserPopup = false"/>
          </Popup>
        </div>
        <TabSelector :tabs="userTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>
    <div class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:all>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek gebruiker'
            :entries="users"
            :headers="UserHeaders"
            :updateFunction="updateFunction"
            :filters="UserFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectUser', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:customer>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek gebruiker'
            :entries="users"
            :headers="UserHeaders"
            :updateFunction="customerUpdateFunction"
            :filters="UserFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectUser', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:employee>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek gebruiker'
            :entries="users"
            :headers="UserHeaders"
            :updateFunction="employeeUpdateFunction"
            :filters="UserFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectUser', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
        <template v-slot:admin>
          <data-table
            generalSearchParameter='generalSearch'
            filterPlaceholder='Zoek gebruiker'
            :entries="users"
            :headers="UserHeaders"
            :updateFunction="adminUpdateFunction"
            :filters="UserFilters"
            :loading="loading">
            <template v-slot:links="item">
              <router-link :to="{name:'InspectUser', params: { id: item.entry.id }}">
                <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
              </router-link>
            </template>
          </data-table>
        </template>
      </TabPanel>

    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent, ref } from 'vue';
import { useStore } from 'vuex';
import DataTable from '@/components/elements/DataTable.vue';
import Footer from '@/components/Footer.vue';
import { UserHeaders, UserFilters } from '@/helpers/paginationFields';
import { createUpdateFunction, createUpdateFunctionExtraParams } from '@/helpers/paginationHelpers';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import Popup from '@/components/elements/popup/popup.vue';
import UserForm from '@/components/user/UserForm.vue';
import { userRoleTranslator } from '@/helpers/fieldTranslators';

export default defineComponent({
  components: {
    DataTable,
    Footer,
    TabPanel,
    TabSelector,
    Popup,
    UserForm,
  },
  setup() {
    const store = useStore();

    const loading = computed(() => store.state.userstore.users.loading);
    const user = computed(() => store.state.authenticationstore.user);

    const users = computed(() => store.state.userstore.users.value
      .map((data) => {
        const res = { ...data };
        res.role = userRoleTranslator[data.role];
        return res;
      }));

    const updateFunction = createUpdateFunction('CLEAR_USERS', 'get_users_pagination');
    const customerUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_USERS',
      'get_users_pagination',
      { role: 'customer' },
    );
    const employeeUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_USERS',
      'get_users_pagination',
      { role: 'employee' },
    );
    const adminUpdateFunction = createUpdateFunctionExtraParams(
      'CLEAR_USERS',
      'get_users_pagination',
      { role: 'admin' },
    );

    const userTabs = [
      {
        name: 'all',
        text: 'Alle',
      },
      {
        name: 'customer',
        text: 'Gebruiker',
      },
      {
        name: 'employee',
        text: 'Magda medewerker',
      },
      {
        name: 'admin',
        text: 'Magda admin',
      },
    ];
    const activeTab = ref('all');

    const showNewUserPopup = ref(false);

    return {
      activeTab,
      userTabs,
      UserFilters,
      UserHeaders,
      users,
      updateFunction,
      loading,
      user,
      customerUpdateFunction,
      employeeUpdateFunction,
      adminUpdateFunction,
      showNewUserPopup,
    };
  },
});
</script>
