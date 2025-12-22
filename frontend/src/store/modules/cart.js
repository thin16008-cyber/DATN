// store/modules/cart.js
export default {
  namespaced: true,
  state: () => ({
    slides: [
      {
        content: "Remember",
        backcontent: "Oboe",
      },
      {
        content: "Remember",
        backcontent: "Oboe",
      },
      {
        content: "Remember",
        backcontent: "Oboe",        
      },
      {
        content: "Remember",  
        backcontent: "Oboe",
      },
      {
        content: "Remember",
        backcontent: "Oboe",
      },
      {
        content: "Remember",
        backcontent: "Oboe",
      },
    ],
  }),
  mutations: {
    setSlides(state, newSlides) {
      state.slides = newSlides;
    },
  },
  getters: {
    slides: (state) => state.slides,
  },
};
