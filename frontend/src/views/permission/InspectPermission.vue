<template>
  <div class="content-wrapper">
    <div v-if="!editing" class="content">
      <div v-if="permission">
        <BackButton/>
        <h3>{{permission.name}}</h3>
        <h4>beschrijving: {{permission.description}}</h4>
        <h4>code: {{permission.code}}</h4>
        <h4>link:</h4>
        <a :href="permission.link">{{permission.link}}</a>
        <h4>Bewijs: </h4>
        <a :href="permission.proof.fileLocation">{{permission.proof.fileLocation}}</a>
        <button v-on:click="deletePermission"
                class="button-ternary-full floatRight">Verwijderen</button>
      </div>
      <div v-else>
        <h3>Machtiging bestaat niet</h3>
      </div>
    </div>
    <PermissionForm v-else :permission="permission"
    @permission_form_close="toggleEditing"/>
    <Footer/>
  </div>
</template>

<script lang=ts>
import { ref, defineComponent, computed } from 'vue';
import { useStore } from 'vuex';
import { useRouter, useRoute } from 'vue-router';
import PermissionForm from '@/components/permission/PermissionForm.vue';
import Footer from '@/components/Footer.vue';
import BackButton from '@/components/elements/BackButton.vue';

export default defineComponent({
  components: {
    PermissionForm,
    Footer,
    BackButton,
  },
  setup() {
    const editing = ref(false);

    const store = useStore();
    const router = useRouter();
    const route = useRoute();

    const permission = computed(() => store.state.permissionstore.permission.value);
    store.dispatch('fetch_permission', route.params.id);

    function deletePermission() {
      store.dispatch('delete_permission', permission.value);
      router.go(-1);
    }

    function toggleEditing() {
      editing.value = !editing.value;
    }

    return {
      editing,
      permission,
      deletePermission,
      toggleEditing,
    };
  },
});
</script>
