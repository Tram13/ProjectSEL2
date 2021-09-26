<template>
  <div class="input-field-container">
    <label class="input-field-title">
      {{ label }}
    </label>
    <div class="input-field-input-container" :class="{ error: showError }">
      <template v-for="(val, index) in values" :key="index">
        <label>
          <input type="checkbox" class="input-field-input"
           v-model="checks" :value="val"
           @blur="onFocusLeave"/>
          {{val}}
        </label>
        <br/>
      </template>
    </div>

    <div class="input-field-error">
      <slot v-if="showError"></slot>
    </div>
  </div>
</template>

<script lang=ts>
/* eslint no-underscore-dangle: 0 */
import { ref, computed } from 'vue';

export default {
  components: {
  },
  props: {
    label: String,
    alwaysShowError: { type: Boolean, default: false },
    values: Array,
    checked: Array,
  },
  emits: ['update'],
  data() {
    return {
      checks: this.$props.checked,
      extraValues: [],
      newValue: '',
    };
  },
  watch: {
    checks(newVal) {
      this.$emit('update', newVal);
    },
  },
  methods: {
    addNewValue() {
      if (this.newValue !== '') {
        this.extraValues.push(this.newValue);
        this.newValue = '';
      }
    },
  },
  setup(props, { emit, slots }) {
    const errorsActivated = ref(false);

    const hasError = computed(() => slots.default
                                    && slots.default().findIndex((o) => o.key !== null) !== -1);

    const onFocusLeave = () => {
      errorsActivated.value = errorsActivated.value || hasError.value;
    };
    const showError = computed(() => ((errorsActivated.value || props.alwaysShowError)
                                        && hasError.value));

    const onInput = (input) => {
      emit('update', input);
    };

    return {
      onFocusLeave,
      showError,
      onInput,
    };
  },
};
</script>

<style scoped lang="sass">
.input-field-input-container
  border: 2px solid rgb(184, 184, 184)
  border-radius: 7px
  overflow: hidden
  width: max-content
  padding: 2px 10px 2px 0

.eye-container
  padding: 10px 20px
  display: flex
  align-items: center
  justify-content: center

  img
    width: 25px
    cursor: pointer
    -webkit-user-drag: none
    user-select: none

.input-field-title
  font-size: 16px
  color: rgb(127, 131, 144)

.input-field-input-container:focus-within
  border-color: var(--secondary-light)
  background: white

.input-field-input
  border: none
  outline: none
  font-size: 16px
  padding: 10px 20px
  background: transparent
  flex: 1

.error
  border-color: rgb(255, 93, 93)
  background: rgb(255, 247, 247)

.input-field-error
  overflow: hidden
  min-height: 25px
  color: rgb(255, 93, 93)
  font-size: 12px
</style>
