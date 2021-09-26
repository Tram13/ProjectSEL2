<!-- http://blog.lucedigitale.com -->
<template>
<div class="speechballoon">
    <textarea
    :value="modelValue"
    variableType="textarea"
    :readonly="userRole === 'customer'"
    :style="{ cursor: userRole === 'customer' ? 'default' : 'auto' }"
    >
    </textarea></div>
</template>

<script lang=ts>
import { computed } from 'vue';
import { useStore } from 'vuex';

export default {
  props: {
    modelValue: String,
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit }) {
    const store = useStore();
    const userRole = computed(() => store.getters.role);

    const onInput = (input) => {
      emit('update:modelValue', input);
    };

    const onChange = () => {
      emit('change');
    };

    return {
      onInput,
      onChange,
      userRole,
    };
  },
};
</script>

<style lang="sass">
.speechballoon
  position: relative
  width: 100%
  min-height: 125px
  border-radius: 10px
  box-shadow: 0 0.125rem 0.5rem rgba(0, 0, 0, 0.2), 0 0.0625rem 0.125rem rgba(0, 0, 0, 0.2)
  border: solid var(--secondary-normal)
  filter: drop-shadow(0 -0.0625rem 0.0625rem rgba(0, 0, 0, 0.1))
  margin: 20px 0 2vh 0

  textarea
    position: inherit
    width: 96%
    height: 96%
    background: inherit
    margin: 2%
    padding: 0
    border: none
    resize: none
    outline: none
</style>
