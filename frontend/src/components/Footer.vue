<template>
  <footer>
    <div v-if="!user" class="footer_left">
      <img src="../assets/logo_vlaanderen_large.svg" alt="logo Vlaanderen"/>
    </div>

    <div class="footer_right">
      <div>
        <h3 v-if="!user">
          Vlaanderen.be is de officiële website van de Vlaamse overheid
        </h3>
        <span>Uitgegeven door Informatie Vlaanderen.</span>

        <ul>
          <li>
            <a href="">Over Magda.be</a>
          </li>
          <li>
            <a href="http://www.vlaanderen.be/intern/disclaimer" rel="noopener" target="_blank">Disclaimer</a>
          </li>
          <li>
            <a href="https://www.vlaanderen.be/intern/cookiebeleid" rel="noopener" target="_blank">Cookieverklaring</a>
          </li>
          <li>
            <a href="http://www.vlaanderen.be/nl/toegankelijkheid" rel="noopener" target="_blank">Toegankelijkheid</a>
          </li>
        </ul>
      </div>

    </div>
  </footer>
</template>

<script>
import { useStore } from 'vuex';
import { computed, defineComponent } from 'vue';

export default defineComponent({
  setup() {
    const store = useStore();
    const user = computed(() => store.state.authenticationstore.user);
    return {
      user,
    };
  },
});
</script>

<style scoped>
  /* Default css */

  footer {
    padding: 2vh 3%;
    width: 94%;
    position: relative;
    background-color: rgb(250, 250, 250);
  }

  footer a {
    font-size: .6em;
    text-decoration: none;
    color: black;
  }

  footer span {
    font-size: .8em;
    width: 100%;
  }

  footer h3 {
    font-size: .9em;
  }

  footer li {
    display: inline-block;
    margin: 0 1vw;
  }

  footer ul {
    display: inline-block;
  }

  .footer_left img {
    width: 100%;
  }

  .footer_left {
    padding: 0 2%;
    max-width: 26%;
  }

  .footer_right {
    padding: 0 2%;
    width: 70%;
  }

  footer > div {
    display: inline-block;
    vertical-align: top;
  }

  /* Media queries breakpoints (responsive) */

  /* Small devices (landscape phones, 576px and up)*/
  @media (max-width: 576px) {
    /* scherm te klein! reset alles en plaats onder elkar */
    footer {
      text-align: left;
    }

    footer h3 {
      font-size: 0.9em;
    }

    footer > div {
      display: block;
    }

    .footer_left, .footer_right {
      max-width: 100%;
      padding: 0;
      width: 100%;
    }

    .footer_left img {
      width: 25%;
      margin: auto;
      display: block;
    }
  }

  /* voor andere media queries (breakpoints) contacteer Tibo Vanheule (ik zet ze niet allemaal in
   omdat hiet  niet nodig is, uselless css ) */

</style>
