<script lang=ts>
import { h } from 'vue';

export default ({
  props: {
    activePage: { type: String, required: true },
  },
  render() {
    const regularSlot = this.$slots && this.$slots[this.activePage];
    return h(() => regularSlot());
  },
});
</script>
