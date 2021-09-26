<template>
  <div class="content-container">
    <div class="content">
      <div class="title-bar">
      <h1>Machtiging</h1>
      <router-link :to="{name: 'CreatePermission'}" class="right">
        <button class="button-secondary-edge">Machtiging Aanmaken</button>
      </router-link>
      </div>
      <data-table
        filterPlaceholder='Zoek machtiging'
        :filters="PermissionFilters"
        :entries="permissions"
        :headers="PermissionHeaders"
        :updateFunction="updateFunction"
        :loading="loading">
        <template v-slot:links="item">
          <router-link :to="{name:'InspectPermission', params: { id: item.entry.id }}">
            <img src="@/assets/right.svg" alt="arrow-right" class="arrow"/>
          </router-link>
        </template>
      </data-table>
    </div>
    <Footer/>
  </div>
</template>

<script lang="ts">
import { computed, defineComponent } from 'vue';
import { useStore } from 'vuex';
import DataTable from '@/components/elements/DataTable.vue';
import Footer from '@/components/Footer.vue';
import { createUpdateFunction } from '@/helpers/paginationHelpers';
import { PermissionHeaders, PermissionFilters } from '@/helpers/paginationFields';

export default defineComponent({
  components: {
    DataTable,
    Footer,
  },
  setup() {
    const store = useStore();
    const permissions = computed(() => store.state.permissionstore.permissions.value);
    const loading = computed(() => store.state.permissionstore.permissions.loading);

    const updateFunction = createUpdateFunction('CLEAR_PERMISSIONS', 'fetch_permissions_pagination');

    return {
      PermissionFilters,
      PermissionHeaders,
      permissions,
      loading,
      updateFunction,
    };
  },
});
</script>
