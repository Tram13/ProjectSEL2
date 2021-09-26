<template>
  <div class="content-container">
    <div class='banner'>
      <div class='banner-content'>
        <div class="title-bar">
          <h1>Statistieken</h1>
        </div>
        <TabSelector :tabs="statsTabs" @tabSelected="(name) => activeTab = name"
                     :selected="activeTab"/>
      </div>
    </div>
    <div class="content">
      <TabPanel :activePage="activeTab">
        <template v-slot:users>
          <div class="title-bar">
            <h3>Aantal gebruikers overheen de tijd</h3>
            <select v-model="dateRange">
              <option value="passed_week">Afgelopen 7 dagen</option>
              <option value="passed_month">Afgelopen 30 dagen</option>
              <option value="passed_year">Agelopen 365 dagen</option>
              <option value="all_time">Alle data</option>
            </select>
          </div>
          <LineChart
            v-if="!usersStatsLoading && !endpointsLoading"
            class="ani1"
            :data="usersStats"/>
        </template>
        <template v-slot:organisations>
          <div class="title-bar">
            <h3>Aantal organisaties overheen de tijd</h3>
            <select v-model="dateRange">
              <option value="passed_week">Afgelopen 7 dagen</option>
              <option value="passed_month">Afgelopen 30 dagen</option>
              <option value="passed_year">Agelopen 365 dagen</option>
              <option value="all_time">Alle data</option>
            </select>
          </div>
          <LineChart
            v-if="!organisationsStatsLoading && !endpointsLoading"
            class="ani1"
            :data="organisationsStats"/>
        </template>
        <template v-slot:proposals>
          <div class="title-bar">
            <h3>Aantal aanvragen overheen de tijd</h3>
            <select v-model="dateRange">
              <option value="passed_week">Afgelopen 7 dagen</option>
              <option value="passed_month">Afgelopen 30 dagen</option>
              <option value="passed_year">Agelopen 365 dagen</option>
              <option value="all_time">Alle data</option>
            </select>
          </div>
          <LineChart
            v-if="!proposalsStatsLoading && !endpointsLoading"
            class="ani1"
            :data="proposalsStats"/>
        </template>
        <template v-slot:services>
          <div class="title-bar">
            <h3>Aantal diensten overheen de tijd</h3>
            <select v-model="dateRange">
              <option value="passed_week">Afgelopen 7 dagen</option>
              <option value="passed_month">Afgelopen 30 dagen</option>
              <option value="passed_year">Agelopen 365 dagen</option>
              <option value="all_time">Alle data</option>
            </select>
          </div>
          <LineChart
            v-if="!servicesStatsLoading && !endpointsLoading"
            class="ani1"
            :data="servicesStats"/>
        </template>
        <template v-slot:packages>
          <div class="title-bar">
            <h3>Aantal pakketten overheen de tijd</h3>
            <select v-model="dateRange">
              <option value="passed_week">Afgelopen 7 dagen</option>
              <option value="passed_month">Afgelopen 30 dagen</option>
              <option value="passed_year">Agelopen 365 dagen</option>
              <option value="all_time">Alle data</option>
            </select>
          </div>
          <LineChart v-if="!packagesStatsLoading && !endpointsLoading"
          class="ani1"
          :data="packagesStats"/>
        </template>
      </TabPanel>

    </div>
    <Footer/>
  </div>
</template>

<script>
import { ref, computed } from 'vue';
import Footer from '@/components/Footer.vue';
import LineChart from '@/components/elements/graphs/lineChart.vue';
import { useStore } from 'vuex';
import TabPanel from '@/components/elements/tabs/tabPanel.vue';
import TabSelector from '@/components/elements/tabs/tabSelector.vue';

export default {
  name: 'Statistics',
  components: {
    Footer,
    LineChart,
    TabPanel,
    TabSelector,
  },
  setup() {
    const statsTabs = [
      {
        name: 'users',
        text: 'Gebruikers',
      },
      {
        name: 'organisations',
        text: 'Organisaties',
      },
      {
        name: 'proposals',
        text: 'Aanvragen',
      },
      {
        name: 'services',
        text: 'Diensten',
      },
      {
        name: 'packages',
        text: 'Pakketten',
      },
    ];
    const activeTab = ref('users');

    const store = useStore();

    // wacht tot de endpoints voor statistieken opgehaald zijn
    (async () => {
      await store.dispatch('fetch_stats_endpoints');
      store.dispatch('fetch_users_stats');
      store.dispatch('fetch_organisations_stats');
      store.dispatch('fetch_proposals_stats');
      store.dispatch('fetch_services_stats');
      store.dispatch('fetch_packages_stats');
    })();

    const endpointsLoading = computed(
      () => store.state.statisticsstore.statsEndpoints.loading,
    );

    const dateRange = ref('passed_week');
    const to = new Date();

    const numberInTime = (dataPoints, endDate) => {
      const offsetMapping = {
        passed_year: -365,
        passed_month: -30,
        passed_week: -7,
      };

      let newBeginDate;
      if (dateRange.value !== 'all_time') {
        newBeginDate = new Date(
          new Date().setDate(new Date().getDate() + offsetMapping[dateRange.value]),
        );
      }

      if (!dataPoints) {
        return [];
      }
      const dayCountMap = {};
      // needed for all time stats
      let minDate = endDate;
      dataPoints.forEach((dataPoint) => {
        const dataPointDateString = dataPoint.created.split('T')[0];
        const dataPointDate = new Date(dataPointDateString);
        if (dataPointDate < minDate) {
          minDate = dataPointDate;
        }
        dayCountMap[dataPointDateString] = (dayCountMap[dataPointDateString] ?? 0) + 1;
      });

      const processedData = [];
      let cummulativeSum = 0;

      for (let d = newBeginDate ?? minDate; d <= endDate; d.setDate(d.getDate() + 1)) {
        const dataPointDateString = d.toISOString().split('T')[0];
        cummulativeSum += dayCountMap[dataPointDateString] ?? 0;
        processedData.push({
          day: dataPointDateString,
          count: cummulativeSum,
        });
      }
      return processedData;
    };

    const usersStats = computed(
      () => numberInTime(store.state.statisticsstore.usersStats.value, to),
    );
    const usersStatsLoading = computed(
      () => store.state.statisticsstore.usersStats.loading,
    );

    const organisationsStats = computed(
      () => numberInTime(store.state.statisticsstore.organisationsStats.value, to),
    );
    const organisationsStatsLoading = computed(
      () => store.state.statisticsstore.organisationsStats.loading,
    );

    const proposalsStats = computed(
      () => numberInTime(store.state.statisticsstore.proposalsStats.value, to),
    );
    const proposalsStatsLoading = computed(
      () => store.state.statisticsstore.proposalsStats.loading,
    );

    const servicesStats = computed(
      () => numberInTime(store.state.statisticsstore.servicesStats.value, to),
    );
    const servicesStatsLoading = computed(
      () => store.state.statisticsstore.servicesStats.loading,
    );

    const packagesStats = computed(
      () => numberInTime(store.state.statisticsstore.packagesStats.value, to),
    );
    const packagesStatsLoading = computed(
      () => store.state.statisticsstore.packagesStats.loading,
    );

    return {
      dateRange,
      numberInTime,
      endpointsLoading,
      activeTab,
      statsTabs,

      usersStats,
      usersStatsLoading,
      organisationsStats,
      organisationsStatsLoading,
      proposalsStats,
      proposalsStatsLoading,
      servicesStats,
      servicesStatsLoading,
      packagesStats,
      packagesStatsLoading,
    };
  },
};
</script>

<style scoped>

</style>
