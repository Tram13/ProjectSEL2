<script>
import { h } from 'vue';

export default {
  props: {
    headers: Object,
    entry: Object,
    index: Number,
  },
  render() {
    const columns = this.headers.map((header) => {
      const children = [];
      const slotName = header.propName;
      const regularSlot = this.$slots && this.$slots[slotName];

      if (regularSlot) {
        children.push(regularSlot({ entry: this.entry, index: this.index }));
      } else {
        const value = this.entry[header.propName];
        children.push(value == null ? value : String(value));
      }
      return (h('td', { class: 'col table-cell', 'data-label': `${header.name}:` }, children));
    });
    return (h('tr', {}, columns));
  },
};
</script>

<style scoped lang="sass">
.table-cell
  height: 24px
</style>
