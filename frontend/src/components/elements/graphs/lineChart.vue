<template>
  <div>
    <svg width="100%" height="100%" viewBox="0 0 800 330"
    preserveAspectRatio="xMidYMid meet" >
      <g class='lineChart' v-bind:transform="translate">
        <axis class='yA'
          v-bind:scales="yAxis"
          v-bind:chartDefaults='chartDefaults'
          v-bind:data='processedData'
          v-bind:trns='trnsY'/>
        <axis class='xA'
          v-bind:scales="xAxis"
          v-bind:chartDefaults='chartDefaults'
          v-bind:data='processedData'
          v-bind:trns='trnsX()'/>
        <axis class='grid'
          v-bind:scales="yGrid"
          v-bind:chartDefaults='chartDefaults'
          v-bind:data='processedData' v-bind:trns='trnsY'
          v-bind:style="{opacity: chartDefaults.gridOpacity}"/>
        <path class='line' :d="line" />
      </g>
    </svg>

  </div>
</template>

<script>
import * as d3 from 'd3';
import { computed, watch } from 'vue';
import Axis from './axis.vue';

export default {
  name: 'vue-line-chart',
  props: {
    data: Array,
  },
  components: {
    axis: Axis, //  Using reusable component to draw x,y axis and Grid.
  },
  // mounted() {
  //   // Kick off drawing chart once component is mounted
  //   this.calculatePath();
  // },
  setup(props) {
    // let data = [
    //   {
    //     day: '2016-01-11',
    //     count: 80,
    //   },
    //   {
    //     day: '2016-02-12',
    //     count: 250,
    //   },
    //   {
    //     day: '2016-03-13',
    //     count: 150,
    //   },
    //   {
    //     day: '2016-04-14',
    //     count: 496,
    //   },
    //   {
    //     day: '2016-05-15',
    //     count: 140,
    //   },
    //   {
    //     day: '2016-06-16',
    //     count: 380,
    //   },
    //   {
    //     day: '2016-07-17',
    //     count: 140,
    //   },
    //   {
    //     day: '2016-08-17',
    //     count: 240,
    //   },
    //   {
    //     day: '2016-09-18',
    //     count: 100,
    //   },
    //   {
    //     day: '2016-10-18',
    //     count: 260,
    //   },
    //   {
    //     day: '2016-11-18',
    //     count: 100,
    //   },
    //   {
    //     day: '2016-12-18',
    //     count: 150,
    //   },
    // ];
    const chartDefaults = {
      width: 800,
      height: 300,
      chartId: 'linechart-vue',
      margin: {
        top: 5,
        right: 5,
        bottom: 15,
        left: 50,
      },
      gridOpacity: 1,
      data: [],
    };

    const getTrnsx = () => {
      // getTrnsx() {
      // works out translate value in realtive to dynamic height
      const t = `translate(0, ${chartDefaults.height})`;
      return t;
    };

    // Translate co-ords for chart, x axis and yaxis. This is injected into template
    const translate = 'translate(50, 5)';
    const trnsY = 'translate(0,0)';
    const trnsX = getTrnsx;
    const toggleClass = true;

    //  All the maths to work chart co ordinates and woring out Axis
    const parseDate = d3.timeParse('%Y-%m-%d');

    const processedData = computed(
      () => props.data.map((d) => Object({ ...d, date: parseDate(d.day) })),
    );

    const x = computed(() => d3
      .scaleTime()
      .domain(
        d3.extent(processedData.value, (d) => d.date),
      )
      .rangeRound([0, chartDefaults.width - 100]));
    const y = computed(() => d3
      .scaleLinear()
      .domain([
        0,
        d3.max(processedData.value, (d) => d.count + 50),
      ])
      .range([chartDefaults.height, 0]));

    d3.axisBottom().scale(x.value);
    watch(x, (test) => d3.axisBottom().scale(test));
    d3.axisLeft().scale(y.value);
    watch(y, (newVal) => {
      d3.axisLeft().scale(newVal);
    });

    // Key funtions to draw X-axis,YAxis and the grid. All uses component axis
    // play around with time format to get it to display as you want : d3.timeFormat("%b-%d")
    const xAxis = computed(() => d3.axisBottom().scale(x.value));

    const yAxis = computed(() => d3
      .axisLeft()
      .scale(y.value));
    const yGrid = computed(() => d3
      .axisLeft()
      .scale(y.value)
      .tickSize(-(chartDefaults.width - 100), 0, 0)
      .tickFormat(''));
    //  Return the key calculations and functions to draw the chart

    const calculatePath = (pathData) => {
      // Get key calculation funtions to draw chart , Ie scale, axis mapping and plotting
      //  Define calcultion to draw chart
      const path = d3
        .line()
        .x((d) => x.value(d.date))
        .y((d) => y.value(d.count));

      //  draw line then this.line is injected into the template

      return path(pathData);
    };

    const line = computed(() => calculatePath(processedData.value));

    return {
      x,
      y,
      xAxis,
      yAxis,
      yGrid,
      processedData,
      chartDefaults,
      line,
      translate,
      trnsY,
      trnsX,
      toggleClass,
      getTrnsx,
      calculatePath,
    };
  },
};
</script>
<!-- css loaderhttps:// vue-loader.vuejs.org/guide/scoped-css.html#mixing-local-and-global-styles -->
<style>
text {
  color: rgb(73, 73, 73);
}

path.line {
  fill: none;
  stroke: var(--secondary-normal);
  stroke-width: 3px;
}

.grid line {
  opacity: 0.05;
}
.xA line {
  opacity: 0.5;
}

/*Some fancy animation to draw chart*/
svg .lineChart > path {
  stroke: var(--secondary-normal);
  stroke-width: 3;
  stroke-dasharray: 4813.713;
  stroke-dashoffset: 4813.713;
  -webkit-animation-name: draw2;
  animation-name: draw2;
  -webkit-animation-duration: 1s;
  animation-duration: 1s;
  -webkit-animation-fill-mode: forwards;
  animation-fill-mode: forwards;
  -webkit-animation-iteration-count: 1;
  animation-iteration-count: 1;
  -webkit-animation-timing-function: linear;
  animation-timing-function: linear;
}

.ani2 svg .lineChart > path {
  stroke: var(--secondary-normal);
  -webkit-animation-name: draw-2;
  animation-name: draw-2;
}
.ani1 svg .lineChart > path {
  stroke: var(--secondary-normal);
  -webkit-animation-name: draw;
  animation-name: draw;
}
#Layer_1 {
  width: 100%;
}
@-webkit-keyframes draw {
  85% {
  }
  100% {
    stroke-dashoffset: 0;
  }
}
@keyframes draw {
  85% {
  }
  100% {
    stroke-dashoffset: 0;
  }
}

@-webkit-keyframes draw-2 {
  85% {
  }
  100% {
    stroke-dashoffset: 0;
  }
}

@keyframes draw-2 {
  85% {
  }
  100% {
    stroke-dashoffset: 0;
  }
}
.text {
  display: inline-block;
  font-size: 3vw;
  margin: 0.5vw 0 1.5vw;
}

svg {

}
</style>
