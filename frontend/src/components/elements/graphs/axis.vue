<template>
  <g :transform='trns'>
                <g ref="axis"  >
                </g>
</g>
</template>

<script>
import * as d3 from 'd3';
import {
  ref,
  computed,
  onMounted,
  watch,
} from 'vue';

export default {
  name: 'axis',
  props: {
    scales: Function,
    chartDefaults: Object,
    data: Array,
    trns: String,
  },
  setup(props) {
    const axisScales = computed(() => props.scales);
    const axis = ref(null);

    // watch(axisScales, (newScales) => axis.value && d3.select(axis).call(newScales));

    // d3.select(axis).call(axisScales.value);

    onMounted(() => {
      // const node = this.axis;
      d3.select(axis.value).call(axisScales.value);
      watch(axisScales, (newScales) => d3.select(axis.value).call(newScales));
      // watch()
      // watch(axisScales, (newScales) => d3.select(axis).call(newScales));
    });
    return {
      axisScales,
      axis,
    };
  },
};
</script>

<style>
.yA path,
.grid path,
.yA .tick line {
  stroke: transparent;
}
</style>
