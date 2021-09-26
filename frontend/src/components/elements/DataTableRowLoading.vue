<script>
import { h } from 'vue';
import TextSkeleton from '@/components/elements/skeletonLoader/textLoader.vue';

export default {
  components: {
    TextSkeleton,
  },
  props: {
    headers: Object,
    index: Number,
  },
  render() {
    const columns = this.headers.map(() => {
      const children = [h(TextSkeleton)];
      return (h('td', { class: 'col' }, children));
    });
    return (h('tr', {}, columns));
  },
};
</script>
