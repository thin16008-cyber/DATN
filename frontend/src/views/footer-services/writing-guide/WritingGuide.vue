<template>
  <div class="writing-guide-page">
    <div class="writing-guide-container">
      <div class="writing-guide-header">
        <h1>{{ t('writingGuide.title') }}</h1>
        <p class="last-updated">{{ t('writingGuide.lastUpdated') }} {{ lastUpdated }}</p>
      </div>

      <div class="writing-guide-content">
        <section class="guide-section">
          <h2>{{ t('writingGuide.overview.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.overview.writingSystems.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.overview.writingSystems.systems.basicWords') }}</li>
              <li>{{ t('writingGuide.overview.writingSystems.systems.properNouns') }}</li>
              <li>{{ t('writingGuide.overview.writingSystems.systems.idioms') }}</li>
            </ul>
          </div>
          <h2>{{ t('writingGuide.distinction.title') }}</h2>
          <div class="sub-section">
            <p class="example-text">{{ t('writingGuide.distinction.context.example.sentence') }}</p>
            <p class="example-explain" v-html="t('writingGuide.distinction.context.example.explanation')"></p>
          </div>
        </section>


        <section class="guide-section">
          <h2>{{ t('writingGuide.learningMethods.title') }}</h2>
          <div class="sub-section">
            <h3>{{ t('writingGuide.learningMethods.studyOrder.title') }}</h3>
            <ol>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.basicFirst') }}</li>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.verbsSecond') }}</li>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.combinedPractice') }}</li>
              <li>{{ t('writingGuide.learningMethods.studyOrder.steps.idiomsParallel') }}</li>
            </ol>
          </div>
          <div class="sub-section">
            <h3>{{ t('writingGuide.learningMethods.memoryTechniques.title') }}</h3>
            <ul>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.flashcards') }}</li>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.practiceWriting') }}</li>
              <li>{{ t('writingGuide.learningMethods.memoryTechniques.techniques.realTexts') }}</li>
            </ul>
          </div>
        </section>

      </div>
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n';

const { t } = useI18n();
const lastUpdated = ref('15/12/2024');
</script>

<style lang="scss" scoped>
@use '@/views/footer-services/writing-guide/WritingGuide.scss';
</style>