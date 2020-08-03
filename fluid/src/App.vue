<template>
  <v-app id="app">
    <div>
      <v-app-bar color="transparent" elevation="0" height="65px">
        <v-toolbar-title>
          <button
            class="nav ml-16"
            :class="{ activeButton: path == '/' }"
            @click="route('/')"
          >P5 Fluid Simulation</button>
        </v-toolbar-title>

        <v-spacer />
        <button class="nav mr-16">Uriyah Ann</button>
      </v-app-bar>
      <div class="d-flex justify-content-center" id="p5Canvas"></div>
      <v-main>
        <router-view />
      </v-main>
    </div>
  </v-app>
</template>

<script>
export default {
  name: "App",

  components: {},

  data: () => ({
    //
  }),
  mounted() {
    const script = function (p5) {
      var speed = 2;
      var posX = 0;

      // NOTE: Set up is here
      p5.setup = () => {
        //p5.createCanvas(500, 500);
        var canvas = p5.createCanvas(500, 500)
        canvas.parent("p5Canvas");
        p5.ellipse(p5.width / 2, p5.height / 2, 500, 500);
      };
      // NOTE: Draw is here
      p5.draw = () => {
        p5.background(0);
        const degree = p5.frameCount * 3;
        const y = p5.sin(p5.radians(degree)) * 50;

        p5.push();
        p5.translate(0, p5.height / 2);
        p5.ellipse(posX, y, 50, 50);
        p5.pop();
        posX += speed;

        if (posX > p5.width || posX < 0) {
          speed *= -1;
        }
      };
    };
    // NOTE: Use p5 as an instance mode
    const P5 = require("p5");
    new P5(script);
  },
  methods: {
    route(path) {
      if (this.$route.path != path) {
        this.$router.push(path);
      }
    },
  },
  computed: {
    path() {
      return this.$route.path;
    },
  },
};
</script>

<style scoped>
@import url("https://fonts.googleapis.com/css2?family=Mulish:ital,wght@0,400;1,700&display=swap");

#app {
  margin: 0;
}
.nav {
  text-transform: uppercase;
  font-family: "Mulish", sans-serif;
  font-weight: 500;
  font-style: italic;
}
#p5Canvas {
  align-content: center !important;
  justify-content: center !important;
}
</style>
