<template>
  <div class="input-field-container">
    <label class="input-field-title">
      {{ label }}
    </label>
    <div class="input-field-input-container" :class="{ error: showError }">
      <textarea v-if="variableType == 'textarea'"
                :data-testid="dataTestid"
                :value="modelValue"
                @input="onInput($event.target.value)"
                @blur="onFocusLeave"
                @keydown="onChange"
                :placeholder="placeholder"
                :maxlength="maxlength"
                :autocomplete="autocomplete ? '' : 'new-password'"
                class="input-field-input"
                :disabled='disabled'/>
      <input v-else
             :type="variableType"
             :data-testid="dataTestid"
             :value="modelValue"
             @input="onInput($event.target.value)"
             @blur="onFocusLeave"
             @keydown="onChange"
             :placeholder="placeholder"
             :maxlength="maxlength"
             :autocomplete="autocomplete ? '' : 'new-password'"
             :disabled='disabled'
             class="input-field-input"/>
      <div class="eye-container" v-if="type === 'password'">
        <img src="@/assets/eye.svg" @mousedown="showPassword" @mouseup="hidePassword"
             alt="bekijken"/>
      </div>
    </div>

    <div class="input-field-error">
      <slot v-if="showError && !disabled"></slot>
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
    modelValue: String,
    type: String,
    label: String,
    placeholder: String,
    maxlength: String,
    autocomplete: { type: Boolean, default: true },
    alwaysShowError: { type: Boolean, default: false },
    dataTestid: String,
    disabled: Boolean,
  },
  emits: ['update:modelValue', 'change'],
  setup(props, { emit, slots }) {
    const variableType = ref(props.type);
    const errorsActivated = ref(false);

    const hasError = computed(() => slots.default
                                    && slots.default().findIndex((o) => o.key !== null) !== -1);

    const onFocusLeave = () => {
      errorsActivated.value = errorsActivated.value || hasError.value;
    };
    const showError = computed(() => ((errorsActivated.value || props.alwaysShowError)
                                        && hasError.value));

    const showPassword = () => {
      variableType.value = 'text';
    };

    const hidePassword = () => {
      variableType.value = 'password';
    };

    const onInput = (input) => {
      emit('update:modelValue', input);
    };

    const onChange = () => {
      emit('change');
    };

    return {
      showPassword,
      hidePassword,
      variableType,
      onFocusLeave,
      showError,
      onInput,
      onChange,
    };
  },
};
</script>

<style scoped lang="sass">
.input-field-input-container
  display: flex
  border: 2px solid rgb(184, 184, 184)
  border-radius: 7px
  overflow: hidden
  width: 100%

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

.input-field-input:disabled
  background: rgb(194, 194, 194)
  color: rgb(105, 105, 105)
</style>
