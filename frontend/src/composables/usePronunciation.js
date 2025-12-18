import { ref } from 'vue'

const currentAudio = ref(null)
const voices = ref([])

/**
 * Load voices cho Web Speech API
 */
const loadVoices = () => {
  if (!('speechSynthesis' in window)) return
  voices.value = speechSynthesis.getVoices()
}

if ('speechSynthesis' in window) {
  loadVoices()
  speechSynthesis.onvoiceschanged = loadVoices
}

/**
 * Phát âm từ vựng
 *
 * @param {Object} options
 * @param {string} options.word        - Từ cần phát âm
 * @param {string} options.audioUrl    - URL audio (nếu có)
 * @param {string} options.lang        - en-US | en-GB
 * @param {number} options.rate        - Tốc độ (0.1 – 10)
 * @param {number} options.pitch       - Cao độ (0 – 2)
 */
export function usePronunciation() {

  const stop = () => {
    // Stop HTML audio
    if (currentAudio.value) {
      currentAudio.value.pause()
      currentAudio.value.currentTime = 0
      currentAudio.value = null
    }

    // Stop TTS
    if ('speechSynthesis' in window) {
      speechSynthesis.cancel()
    }
  }

  const speakWithTTS = (word, lang, rate, pitch) => {
    if (!('speechSynthesis' in window)) return

    const utterance = new SpeechSynthesisUtterance(word)
    utterance.lang = lang
    utterance.rate = rate
    utterance.pitch = pitch

    // Chọn voice đúng ngôn ngữ nếu có
    const voice = voices.value.find(v => v.lang === lang)
    if (voice) utterance.voice = voice

    speechSynthesis.speak(utterance)
  }

  const playAudio = (audioUrl) => {
    const audio = new Audio(audioUrl)
    currentAudio.value = audio
    audio.play()
  }

  const pronounce = ({
    word,
    audioUrl = null,
    lang = 'en-US',
    rate = 0.9,
    pitch = 1
  }) => {
    if (!word) return

    stop()

    if (audioUrl) {
      playAudio(audioUrl)
    } else {
      speakWithTTS(word, lang, rate, pitch)
    }
  }

  return {
    pronounce,
    playAudio,
    stop,
    voices
  }
}

