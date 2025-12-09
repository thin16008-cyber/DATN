<template>
  <DetailPage
    type="idiom"
    :item="idiomsData"
    :itemId="idiomId"
    
    mainField="englishPhrase" 
    
    readingField="category" 
    
    meaningField="vietnameseMeaning"
    
    :showRelated="true"
    :relatedItems="relatedIdioms"
    :relatedTitle="t('idiomDetail.relatedIdioms')"
    
    relatedMainField="englishPhrase" 
    
    relatedKeyField="idiomId"
    :emptyRelatedMessage="t('idiomDetail.noRelatedIdioms')"
    :notFoundMessage="t('idiomDetail.notFound')"
    @relatedItemClick="navigateToIdiomDetail"
  />
</template>

<script>
import { ref, watch, onMounted } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import { useI18n } from 'vue-i18n';
import DetailPage from '@/components/layout/detail-search/DetailSearch.vue';
import idiomsApi from '../../../api/modules/idiomsApi';

export default {
  name: 'IdiomDetail',
  components: {
    DetailPage
  },
  setup() {
    const { t } = useI18n();
    const route = useRoute();
    const router = useRouter();
    const idiomId = ref(route.params.id);
    const idiomsData = ref(null);
    const relatedIdioms = ref([]);
    const isLoading = ref(false);

    // Function to fetch idiom data
    const fetchIdiomData = async (id) => {
      try {
        isLoading.value = true;
        const response = await idiomsApi.getById(id);
        idiomsData.value = response;
      } catch (error) {
        console.error('Error fetching idiom data:', error); 
        console.error('Error details:', error.message, error.response) 
        idiomsData.value = null;
      } finally {
        isLoading.value = false;
      }
    };

    // Function to fetch related idioms
    const fetchRelatedIdioms = async (id) => {
      try {
        const response = await idiomsApi.getRelated(id);
        relatedIdioms.value = response || []; 
      } catch (error) {
        console.error('Error fetching related idioms:', error);
        relatedIdioms.value = [];
      }
    };

    // Navigate to idiom detail
    const navigateToIdiomDetail = (item) => {
      router.push({ name: 'IdiomDetail', params: { id: item.idiomId || item.id } }); 
    };

    // Navigate to vocabulary detail
    const navigateToVocabularyDetail = (item) => {
      router.push({ name: 'WordDetail', params: { id: item.vocabId || item.id } }); 
    };

    // Initial fetch
    onMounted(() => {
      if (idiomId.value) {
        fetchIdiomData(idiomId.value);
        fetchRelatedIdioms(idiomId.value);
      }
    });

    // Watch for route changes
    watch(
      () => route.params.id,
      (newId) => {
        if (newId) {
          idiomId.value = newId;
          fetchIdiomData(idiomId.value);
          fetchRelatedIdioms(idiomId.value);
        }
      }
    );

    return {
      t,
      idiomsData,
      relatedIdioms,
      idiomId,
      isLoading,
      navigateToIdiomDetail,
      navigateToVocabularyDetail
    };
  }
};
</script>

<style lang="scss" scoped>
@use '@/views/search-english/idiom-detail/IdiomDetail.scss';
</style>