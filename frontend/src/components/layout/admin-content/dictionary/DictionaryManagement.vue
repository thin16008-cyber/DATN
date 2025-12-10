<template>
  <div class="dictionary-management">
    <div class="page-header">
      <h1>Quản lý từ điển</h1>
      <p>Quản lý từ vựng, ngữ pháp, cụm động từ/thành ngữ và mẫu câu</p>
    </div>

    <!-- Tab Navigation -->
    <div class="tab-navigation">
      <button 
        v-for="tab in tabs" 
        :key="tab.key"
        @click="activeTab = tab.key"
        :class="['tab-btn', { active: activeTab === tab.key }]"
      >
        <i :class="tab.icon"></i>
        {{ tab.label }}
      </button>
    </div>

    <!-- Search and Create Button -->
    <div class="search-filters">
      <div class="search-group">
        <i class="fas fa-search search-icon"></i>
        <input 
          v-model="searchQuery"
          type="text" 
          :placeholder="getSearchPlaceholder()"
          class="search-input"
        >
      </div>

      <button @click="openCreateModal" class="btn-create">
        <i class="fas fa-plus"></i>
        Thêm {{ getCreateButtonText() }}
      </button>
    </div>

    <!-- Content based on active tab -->
    <div class="content-area">
      <!-- Loading State -->
      <div v-if="loading" class="loading-state">
        <div class="loading-spinner"></div>
        <p>Đang tải dữ liệu...</p>
      </div>

      <!-- Error State -->
      <div v-else-if="error" class="error-state">
        <div class="error-icon">⚠️</div>
        <p>{{ error }}</p>
        <button @click="loadVocabulary()" class="retry-btn">Thử lại</button>
      </div>

      <!-- Success Message -->
      <div v-if="successMessage" class="success-state">
        <div class="success-icon">✅</div>
        <p>{{ successMessage }}</p>
      </div>

      <!-- Vocabulary Tab -->
      <div v-else-if="activeTab === 'vocabulary'" class="vocabulary-section">
        <!-- Search Results Info -->
        <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
          <p>Tìm thấy <strong>{{ filteredVocabularyTotal }}</strong> từ vựng cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
          <p v-if="getSearchTotalPages() > 1" class="pagination-info">
            Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredVocabularyTotal) }} trong tổng số {{ filteredVocabularyTotal }} kết quả
          </p>
        </div>
        
        <div class="items-grid">
          <div 
            v-for="item in filteredVocabulary" 
            :key="item.id"
            class="item-card vocabulary-card"
          >
            <div class="card-header">
              <div class="word-info">
                <h3 class="word">{{ item.word }}</h3>
                <div class="reading-info" v-if="item.reading">
                  <strong>Phiên âm:</strong> <span class="reading">{{ item.reading }}</span>
                </div>
                <span class="level-badge" :class="item.level ? item.level.toLowerCase() : ''">{{ item.level }}</span>
              </div>
              <div class="actions">
                <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                  <i class="fas fa-edit"></i>
                </button>
                <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                  <i class="fas fa-trash"></i>
                </button>
              </div>
            </div>
            <div class="card-content">
              <p class="meaning">{{ item.meaning }}</p>
              <div class="meta-info">
                <span class="type">{{ item.type }}</span>
                <span class="script-type">{{ item.scriptType }}</span>
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredVocabularyTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
            <button 
              @click="loadVocabulary(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} từ vựng)
            </span>
            
            <button 
              @click="loadVocabulary(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Grammar Tab -->
      <div v-if="activeTab === 'grammar'" class="grammar-section">
        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu ngữ pháp...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle error-icon"></i>
          <p>{{ error }}</p>
          <button @click="loadGrammar()" class="retry-btn">Thử lại</button>
        </div>
        
        <!-- Grammar data -->
        <div v-else>
          <!-- Search Results Info -->
          <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
            <p>Tìm thấy <strong>{{ filteredGrammarTotal }}</strong> ngữ pháp cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
            <p v-if="getSearchTotalPages() > 1" class="pagination-info">
              Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredGrammarTotal) }} trong tổng số {{ filteredGrammarTotal }} kết quả
            </p>
          </div>
          
          <div class="items-grid">
            <div 
              v-for="item in filteredGrammar" 
              :key="item.id"
              class="item-card grammar-card"
            >
              <div class="card-header">
                <div class="grammar-info">
                  <h3 class="structure">{{ item.structure }}</h3>
                  <span class="grammar-type-badge" :class="item.grammarType">{{ getGrammarTypeName(item.grammarType) }}</span>
                </div>
                <div class="actions">
                  <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="card-content">
                <p class="explanation">{{ item.explanation }}</p>
                <div class="pronunciation">
                  <strong>Phát âm:</strong> {{ item.vietnamesePronunciation }}
                </div>
                <div class="example">
                  <strong>Ví dụ:</strong> {{ item.example }}
                </div>
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredGrammarTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
            <button 
              @click="loadGrammar(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} ngữ pháp)
            </span>
            
            <button 
              @click="loadGrammar(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- idioms Tab -->
      <div v-if="activeTab === 'idioms'" class="idioms-section">
        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu idioms...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle error-icon"></i>
          <p>{{ error }}</p>
          <button @click="loadKanji()" class="retry-btn">Thử lại</button>
        </div>
        
        <!-- idioms data -->
        <div v-else>
          <!-- Search Results Info -->
          <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
            <p>Tìm thấy <strong>{{ filteredKanjiTotal }}</strong> hán tự cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
            <p v-if="getSearchTotalPages() > 1" class="pagination-info">
              Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredKanjiTotal) }} trong tổng số {{ filteredKanjiTotal }} kết quả
            </p>
          </div>
          
          <div class="items-grid">
            <div 
              v-for="item in filteredKanji" 
              :key="item.id"
              class="item-card idioms-card"
            >
              <div class="card-header">
                <div class="idioms-info">
                  <h3 class="character">{{ item.character }}</h3>
                </div>
                <div class="actions">
                  <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="card-content">
                <div class="pronunciation">
                  <strong>Phát âm:</strong> {{ item.vietnamesePronunciation }}
                </div>
                <p class="meaning">{{ item.meaning }}</p>
                <div class="strokes">{{ item.strokes }} nét</div>
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredKanjiTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
            <button 
              @click="loadKanji(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} idioms)
            </span>
            
            <button 
              @click="loadKanji(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>

      <!-- Sentences Tab -->
      <div v-if="activeTab === 'sentences'" class="sentences-section">
        <!-- Loading state -->
        <div v-if="loading" class="loading-state">
          <div class="loading-spinner"></div>
          <p>Đang tải dữ liệu mẫu câu...</p>
        </div>
        
        <!-- Error state -->
        <div v-else-if="error" class="error-state">
          <i class="fas fa-exclamation-triangle error-icon"></i>
          <p>{{ error }}</p>
          <button @click="loadSentences()" class="retry-btn">Thử lại</button>
        </div>
        
        <!-- Sentences data -->
        <div v-else>
          <!-- Search Results Info -->
          <div v-if="searchQuery && searchQuery.trim()" class="search-results-info">
            <p>Tìm thấy <strong>{{ filteredSentencesTotal }}</strong> mẫu câu cho từ khóa "<strong>{{ searchQuery }}</strong>"</p>
            <p v-if="getSearchTotalPages() > 1" class="pagination-info">
              Hiển thị {{ searchCurrentPage * searchPageSize + 1 }} - {{ Math.min((searchCurrentPage + 1) * searchPageSize, filteredSentencesTotal) }} trong tổng số {{ filteredSentencesTotal }} kết quả
            </p>
          </div>
          
          <div class="items-grid">
            <div 
              v-for="item in filteredSentences" 
              :key="item.id"
              class="item-card sentence-card"
            >
              <div class="card-header">
                <div class="sentence-info">
                  <h3 class="japanese-text">{{ item.japanese }}</h3>
                </div>
                <div class="actions">
                  <button @click="editItem(item)" class="btn-edit" title="Chỉnh sửa">
                    <i class="fas fa-edit"></i>
                  </button>
                  <button @click="deleteItem(item.id)" class="btn-delete" title="Xóa">
                    <i class="fas fa-trash"></i>
                  </button>
                </div>
              </div>
              <div class="card-content">
                <p class="vietnamese-meaning">{{ item.vietnamese }}</p>
              </div>
            </div>
          </div>
          
          <!-- Search Pagination -->
          <div v-if="searchQuery && searchQuery.trim() && getSearchTotalPages() > 1" class="pagination">
            <button 
              @click="prevSearchPage()" 
              :disabled="searchCurrentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ searchCurrentPage + 1 }} / {{ getSearchTotalPages() }}
              ({{ filteredSentencesTotal }} kết quả)
            </span>
            
            <button 
              @click="nextSearchPage()" 
              :disabled="searchCurrentPage >= getSearchTotalPages() - 1"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
          
          <!-- Regular Pagination -->
          <div v-if="totalPages > 1 && (!searchQuery || !searchQuery.trim())" class="pagination">
            <button 
              @click="loadSentences(currentPage - 1)" 
              :disabled="currentPage === 0"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-left"></i>
            </button>
            
            <span class="pagination-info">
              Trang {{ currentPage + 1 }} / {{ totalPages }}
              ({{ totalElements }} mẫu câu)
            </span>
            
            <button 
              @click="loadSentences(currentPage + 1)" 
              :disabled="isLastPage"
              class="pagination-btn"
            >
              <i class="fas fa-chevron-right"></i>
            </button>
          </div>
        </div>
      </div>
    </div>



    <!-- Create/Edit Modal -->
    <div v-if="showModal" class="modal-overlay" @click="closeModal">
      <div class="modal-content" @click.stop>
        <div class="modal-header">
          <h3>{{ modalMode === 'edit' ? 'Chỉnh sửa' : 'Thêm mới' }} {{ getModalTitle() }}</h3>
          <button @click="closeModal" class="btn-close">
            <i class="fas fa-times"></i>
          </button>
        </div>
        
        <div class="modal-body">
          <!-- Error Message in Modal -->
          <div v-if="error" class="modal-error">
            <i class="fas fa-exclamation-triangle"></i>
            <span>{{ error }}</span>
          </div>
          
          <div v-if="activeTab === 'vocabulary'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="words">Từ vựng *</label>
                <input 
                  id="words"
                  v-model="editingItem.words" 
                  type="text" 
                  placeholder="Nhập từ vựng"
                  required
                />
              </div>
              <div class="form-field">
                <label for="phoneticIpa">Phiên âm IPA</label>
                <input 
                  id="phoneticIpa"
                  v-model="editingItem.phoneticIpa" 
                  type="text" 
                  placeholder="Nhập phiên âm (VD: /ɪɡˈzæmpəl/)"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="vietnameseMeaning">Nghĩa tiếng Việt *</label>
                <textarea 
                  id="vietnameseMeaning"
                  v-model="editingItem.vietnameseMeaning" 
                  placeholder="Nhập nghĩa tiếng Việt"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="wordType">Loại từ</label>
                <select id="wordType" v-model="editingItem.wordType">
                  <option value="">Chọn loại từ</option>
                  <option value="noun">Danh từ</option>
                  <option value="verb">Động từ</option>
                  <option value="adjective">Tính từ</option>
                  <option value="adverb">Trạng từ</option>
                  <option value="preposition">Giới từ</option>
                  <option value="conjunction">Liên từ</option>
                  <option value="pronoun">Đại từ</option>
                </select>
              </div>
              <div class="form-field">
                <label for="level">Mức độ (CEFR)</label>
                <select id="level" v-model="editingItem.level">
                  <option value="">Chọn mức độ</option>
                  <option value="A1">A1</option>
                  <option value="A2">A2</option>
                  <option value="B1">B1</option>
                  <option value="B2">B2</option>
                  <option value="C1">C1</option>
                  <option value="C2">C2</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="audioUrl">Đường dẫn Audio (URL)</label>
                <input 
                  id="audioUrl"
                  v-model="editingItem.audioUrl" 
                  type="url" 
                  placeholder="Dán URL file audio"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-field">
                <label for="synonyms">Từ đồng nghĩa</label>
                <textarea 
                  id="synonyms"
                  v-model="editingItem.synonyms" 
                  placeholder="Nhập các từ đồng nghĩa, cách nhau bằng dấu phẩy"
                  rows="2"
                ></textarea>
              </div>
              <div class="form-field">
                <label for="antonyms">Từ trái nghĩa</label>
                <textarea 
                  id="antonyms"
                  v-model="editingItem.antonyms" 
                  placeholder="Nhập các từ trái nghĩa, cách nhau bằng dấu phẩy"
                  rows="2"
                ></textarea>
              </div>
            </div>

            </div>
          
          <div v-else-if="activeTab === 'idioms'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="englishPhrase">Thành ngữ/Cụm từ (Tiếng Anh) *</label>
                <input 
                  id="englishPhrase"
                  v-model="editingItem.englishPhrase" 
                  type="text" 
                  placeholder="Nhập thành ngữ hoặc cụm từ Tiếng Anh"
                  required
                />
              </div>
              <div class="form-field">
                <label for="category">Phân loại</label>
                <select id="category" v-model="editingItem.category">
                  <option value="">Chọn phân loại</option>
                  <option value="idiom">Thành ngữ</option>
                  <option value="phrase">Cụm từ</option>
                  <option value="proverb">Tục ngữ</option>
                  <option value="slang">Tiếng lóng</option> </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="audioUrl">Đường dẫn Audio (URL)</label>
                <input 
                  id="audioUrl"
                  v-model="editingItem.audioUrl" 
                  type="url" 
                  placeholder="Dán URL file audio"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="vietnameseMeaning">Nghĩa tiếng Việt *</label>
                <textarea 
                  id="vietnameseMeaning"
                  v-model="editingItem.vietnameseMeaning" 
                  placeholder="Nhập nghĩa tiếng Việt của thành ngữ/cụm từ"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="origin">Nguồn gốc/Giải thích chi tiết</label>
                <textarea 
                  id="origin"
                  v-model="editingItem.origin" 
                  placeholder="Nhập nguồn gốc hoặc giải thích chi tiết"
                  rows="3"
                ></textarea>
              </div>
            </div>
          </div>
          
          <!-- Grammar Form -->
          <div v-else-if="activeTab === 'grammar'" class="form-group">
            <div class="form-row">
              <div class="form-field">
                <label for="structure">Cấu trúc ngữ pháp *</label>
                <input 
                  id="structure"
                  v-model="editingItem.structure" 
                  type="text" 
                  placeholder="Nhập cấu trúc ngữ pháp (VD: Subject + Verb + Object)"
                  required
                />
              </div>
              <div class="form-field">
                <label for="grammarType">Loại ngữ pháp</label>
                <select id="grammarType" v-model="editingItem.grammarType">
                  <option value="">Chọn loại ngữ pháp</option>
                  <option value="tense">Thì (Tense)</option>
                  <option value="conditional">Câu điều kiện</option>
                  <option value="passive">Bị động</option>
                  <option value="modal">Động từ khuyết thiếu</option>
                  <option value="relative">Mệnh đề quan hệ</option>
                  <option value="report">Câu tường thuật</option>
                </select>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="tenseAspect">Thì/Thể (Tense/Aspect)</label>
                <input 
                  id="tenseAspect"
                  v-model="editingItem.tenseAspect" 
                  type="text" 
                  placeholder="VD: Hiện tại hoàn thành, Tiếp diễn, ..."
                />
              </div>

              <div class="form-field">
                <label for="topicTag">Chủ đề/Tag</label>
                <input 
                  id="topicTag"
                  v-model="editingItem.topicTag" 
                  type="text" 
                  placeholder="Tag để tìm kiếm nhanh (VD: A1, TOEIC, GMAT)"
                />
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="explanation">Giải thích cơ bản *</label>
                <textarea 
                  id="explanation"
                  v-model="editingItem.explanation" 
                  placeholder="Nhập giải thích ngữ pháp ngắn gọn"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field">
                <label for="detailContent">Nội dung chi tiết/Ví dụ</label>
                <textarea 
                  id="detailContent"
                  v-model="editingItem.detailContent" 
                  placeholder="Nhập các ví dụ, ghi chú, và nội dung chi tiết (thay thế cho trường Ví dụ cũ)"
                  rows="5"
                ></textarea>
              </div>
            </div>
          </div>

          <!-- SimpleSentence -->
          <div v-else-if="activeTab === 'sentences'" class="form-group">
            <div class="form-row">
              <div class="form-field full-width">
                <label for="englishSentence">Câu tiếng Anh *</label>
                <textarea 
                  id="englishSentence"
                  v-model="editingItem.englishSentence" 
                  placeholder="Nhập câu tiếng Anh"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>
            
            <div class="form-row">
              <div class="form-field full-width">
                <label for="vietnameseTranslation">Nghĩa tiếng Việt *</label>
                <textarea 
                  id="vietnameseTranslation"
                  v-model="editingItem.vietnameseTranslation" 
                  placeholder="Nhập nghĩa tiếng Việt"
                  rows="3"
                  required
                ></textarea>
              </div>
            </div>

            <div class="form-row">
              <div class="form-field">
                <label for="audioUrl">Đường dẫn Audio (URL)</label>
                <input 
                  id="audioUrl"
                  v-model="editingItem.audioUrl" 
                  type="url" 
                  placeholder="Dán URL file audio"
                />
              </div>
              <div class="form-field">
                <label for="difficulty">Mức độ khó (CEFR/Khác)</label>
                <input 
                  id="difficulty"
                  v-model="editingItem.difficulty" 
                  type="text" 
                  placeholder="VD: A1, B2, C1"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-field">
                <label for="relatedVocabId">ID từ vựng liên quan (UUID)</label>
                <input 
                  id="relatedVocabId"
                  v-model="editingItem.relatedVocabId" 
                  type="text" 
                  placeholder="Dán ID từ vựng liên quan"
                />
              </div>
              <div class="form-field">
                <label for="relatedIdiomId">ID thành ngữ liên quan (UUID)</label>
                <input 
                  id="relatedIdiomId"
                  v-model="editingItem.relatedIdiomId" 
                  type="text" 
                  placeholder="Dán ID thành ngữ liên quan"
                />
              </div>
            </div>

            <div class="form-row">
              <div class="form-field">
                <label for="relatedGrammarId">ID ngữ pháp liên quan (UUID)</label>
                <input 
                  id="relatedGrammarId"
                  v-model="editingItem.relatedGrammarId" 
                  type="text" 
                  placeholder="Dán ID ngữ pháp liên quan"
                />
              </div>
              </div>

          </div>
          
          <!-- Other tab forms can be added here -->
          <div v-else class="placeholder-text">
            Form cho {{ getModalTitle() }} sẽ được thêm vào sau...
          </div>
        </div>
        
        <div class="modal-footer">
          <button @click="closeModal" class="btn-cancel">Hủy</button>
          <button @click="saveItem" class="btn-save">
            {{ modalMode === 'edit' ? 'Cập nhật' : 'Thêm mới' }}
          </button>
        </div>
      </div>
    </div>

    <!-- Delete Confirmation Popup -->
    <ThePopup
      v-if="showDeleteConfirm"
      title="Xác nhận xóa"
      :message="deleteConfirmMessage"
      confirm-text="Xóa"
      @confirm="confirmDelete"
      @cancel="cancelDelete"
    />
  </div>
</template>

<script setup>
import { ref, computed, watch, onMounted } from 'vue'
import ThePopup from '@/components/common/popup/ThePopup.vue'
import vocabularyApi from '@/api/modules/vocabularyApi'
import idiomsApi from '@/api/modules/idiomsApi'
import grammarApi from '@/api/modules/grammarApi'
import sampleSentenceApi from '@/api/modules/sampleSentenceApi'

// State
const activeTab = ref('vocabulary')
const searchQuery = ref('')
const showModal = ref(false)
const modalMode = ref('create') // 'create' or 'edit'
const editingItem = ref(null)
const itemsPerPage = ref(10)

// Tabs configuration
const tabs = ref([
  { key: 'vocabulary', label: 'Từ vựng', icon: 'fas fa-book' },
  { key: 'grammar', label: 'Ngữ pháp', icon: 'fas fa-language' },
  { key: 'idioms', label: 'Thành ngữ/Cụm từ', icon: 'fas fa-comment-dots' },
  { key: 'sentences', label: 'Mẫu câu', icon: 'fas fa-quote-left' }
])

// Data từ API
const vocabularyData = ref([])
const grammarData = ref([])
const idiomsData = ref([])
const sentencesData = ref([])
const availableIdioms = ref([]) // Danh sách idioms để chọn khi tạo từ vựng
const loading = ref(false)
const error = ref('')
const successMessage = ref('')

// Delete confirmation popup
const showDeleteConfirm = ref(false)
const deleteConfirmMessage = ref('')
const deleteItemId = ref(null)

// All data for search (tất cả dữ liệu để tìm kiếm)
const allVocabularyData = ref([])
const allGrammarData = ref([])
const allIdiomsData = ref([])
const allSentencesData = ref([])

// Pagination
const currentPage = ref(0) // API sử dụng 0-based indexing
const pageSize = ref(10)
const totalPages = ref(0)
const totalElements = ref(0)
const isLastPage = ref(false)

// Search pagination
const searchCurrentPage = ref(0)
const searchPageSize = ref(10)

// API Functions
const loadVocabulary = async (page = 0) => {
   try {
    loading.value = true
    error.value = ''
    const response = await vocabularyApi.getAll({ page, size: pageSize.value })
    vocabularyData.value = response.vocabularies.map(item => {
      return {
        id: item.vocabularyId,
        word: item.words,
        // Đổi 'reading' (cách đọc Nhật/Việt) thành 'pronunciation'
        pronunciation: item.pronunciation || '', 
        meaning: item.meanning || item.meaning || item.vietnameseMeaning || '', 
        type: item.wordType,
        // Xóa/Đổi tên 'scriptType' vì không cần thiết cho Tiếng Anh
        // scriptType: item.scriptType, 
        // Xóa 'kanjiId'
        relatedEntryId: item.relatedEntryId || null, // Có thể thay bằng Related ID
        createdAt: new Date().toLocaleDateString('vi-VN')
      }
    })
    
    currentPage.value = response.currentPage
    totalPages.value = response.totalPages
    totalElements.value = response.totalElements
    isLastPage.value = response.isLastPage
    
  } catch (err) {
    error.value = 'Không thể tải dữ liệu từ vựng'
    console.error('Lỗi khi tải từ vựng:', err) // Giữ tiếng Việt cho console error
  } finally {
    loading.value = false
  }
}

    const loadIdioms = async (page = 0) => {
      try {
        loading.value = true
        error.value = ''
        
        const response = await idiomsApi.getAll({ page, size: pageSize.value })
        
        // Giả định API trả về dữ liệu danh sách nằm trong 'content' (chuẩn Spring Page)
        idiomsData.value = response.content.map(item => ({
          // ID là UUID
          id: item.idiomId, 
          
          // Ánh xạ english_phrase sang phrase (tên hiển thị)
          phrase: item.englishPhrase, 
          
          // Ánh xạ vietnamese_meaning sang meaning
          meaning: item.vietnameseMeaning, 
          
          // Giữ nguyên category
          category: item.category, 

            // Có thể thêm các trường khác nếu cần hiển thị nhanh
            // audioUrl: item.audioUrl,
            // origin: item.origin,
            
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
        
        // Cập nhật thông tin phân trang dựa trên chuẩn Spring Page
        currentPage.value = response.currentPage // Hoặc response.number
        totalPages.value = response.totalPages
        totalElements.value = response.totalItems // Hoặc response.totalElements
        isLastPage.value = response.isLastPage // Hoặc response.last
        
      } catch (err) {
        error.value = 'Không thể tải dữ liệu Thành ngữ/Cụm từ'
        console.error('Lỗi khi tải Thành ngữ/Cụm từ:', err)
      } finally {
        loading.value = false
      }
    }

    const loadGrammar = async (page = 0) => {
      try {
        loading.value = true
        error.value = ''
        const response = await grammarApi.getAll({ page, size: pageSize.value })
        
        grammarData.value = response.grammars.map(item => ({
          id: item.grammarId,
          structure: item.structure,
          explanation: item.explanation,
          example: item.example,
          grammarType: item.grammarType,
          // Xóa 'vietnamesePronunciation' và 'readings' (không cần thiết cho ngữ pháp Tiếng Anh)
          // vietnamesePronunciation: item.vietnamesePronunciation,
          // readings: item.readings || [],
          level: item.level, // Có thể thêm trường level cho ngữ pháp
          createdAt: new Date().toISOString()
        }))
        
        currentPage.value = response.currentPage
        totalPages.value = response.totalPages
        totalElements.value = response.totalElements
        isLastPage.value = response.isLastPage
        
      } catch (err) {
        error.value = 'Không thể tải dữ liệu ngữ pháp'
        console.error('Lỗi khi tải ngữ pháp:', err)
      } finally {
        loading.value = false
      }
    }

    const loadSentences = async (page = 0) => {
      try {
        loading.value = true
        error.value = ''
        const response = await sampleSentenceApi.getAll({ page, size: pageSize.value })
        
        sentencesData.value = response.content.map(item => ({
          id: item.id,
          // Đổi 'japanese' thành 'english'
          english: item.englishText,
          vietnamese: item.vietnameseMeaning,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
        
        currentPage.value = response.currentPage
        totalPages.value = response.totalPages
        totalElements.value = response.totalItems
        isLastPage.value = response.isLastPage
        
      } catch (err) {
        error.value = 'Không thể tải dữ liệu mẫu câu'
        console.error('Lỗi khi tải mẫu câu:', err)
      } finally {
        loading.value = false
      }
    }

// Load all data functions for search
const loadAllVocabulary = async () => {
      try {
        const response = await vocabularyApi.getAll({ page: 0, size: 1000 }) // Tải nhiều dữ liệu hơn
        allVocabularyData.value = response.vocabularies.map(item => ({
          id: item.vocalbId,
          word: item.words,
          // Đổi 'reading' thành 'pronunciation' và loại bỏ tham chiếu ngôn ngữ
          pronunciation: item.pronunciation || '', 
          meaning: item.meanning || item.meaning || item.vietnameseMeaning || '',
          type: item.wordType,
          // Loại bỏ 'scriptType' (Không cần thiết cho Tiếng Anh)
          // scriptType: item.scriptType, 
          // Loại bỏ 'kanjiId'
          relatedEntryId: item.relatedEntryId || null,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
      } catch (err) {
        console.error('Lỗi khi tải toàn bộ từ vựng:', err)
      }
    }

    const loadAllGrammar = async () => {
      try {
        const response = await grammarApi.getAll({ page: 0, size: 1000 })
        allGrammarData.value = response.grammars.map(item => ({
          id: item.grammarId,
          structure: item.structure,
          explanation: item.explanation,
          example: item.example,
          grammarType: item.grammarType,
          // Loại bỏ 'vietnamesePronunciation' và 'readings'
          // vietnamesePronunciation: item.vietnamesePronunciation,
          // readings: item.readings || [],
          createdAt: new Date().toISOString()
        }))
      } catch (err) {
        console.error('Lỗi khi tải toàn bộ ngữ pháp:', err)
      }
    }

    // Đổi tên hàm từ loadAllKanji thành loadAllIdioms
    const loadAllIdioms = async () => {
      try {
        // Đổi kanjiApi thành idiomsApi
        const response = await idiomsApi.getAll({ page: 0, size: 1000 })
        // Đổi allKanjiData thành allIdiomsData
        allIdiomsData.value = response.idioms.map(item => ({
          id: item.idiomId,
          // Đổi 'character' (ký tự Hán tự) thành 'phrase' (cụm từ/thành ngữ)
          phrase: item.phraseText, 
          meaning: item.meaning,
          // Loại bỏ 'strokes' và 'vietnamesePronunciation'
          // strokes: item.strokes,
          // vietnamesePronunciation: item.vietnamesePronunciation,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
      } catch (err) {
        console.error('Lỗi khi tải toàn bộ Thành ngữ/Cụm từ:', err)
      }
    }

    const loadAllSentences = async () => {
      try {
        const response = await sampleSentenceApi.getAll({ page: 0, size: 1000 })
        allSentencesData.value = response.content.map(item => ({
          id: item.id,
          // Đổi 'japanese' thành 'english'
          english: item.englishText,
          vietnamese: item.vietnameseMeaning,
          createdAt: new Date().toLocaleDateString('vi-VN')
        }))
      } catch (err) {
        console.error('Lỗi khi tải toàn bộ mẫu câu:', err)
      }
    }

    // Computed properties
    const filteredVocabulary = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allVocabularyData.value.filter(item => {
          // Tìm kiếm theo từ vựng
          const wordMatch = item.word && item.word.toLowerCase().includes(query)
          
          // Tìm kiếm theo nghĩa
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          
          // Đổi 'reading' thành 'pronunciation'
          const pronunciationMatch = item.pronunciation && item.pronunciation.toLowerCase().includes(query)
          
          // Tìm kiếm theo loại từ
          const typeMatch = item.type && item.type.toLowerCase().includes(query)
          
          return wordMatch || meaningMatch || pronunciationMatch || typeMatch // Cập nhật biến tìm kiếm
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return vocabularyData.value
    })

    const filteredVocabularyTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allVocabularyData.value.filter(item => {
          const wordMatch = item.word && item.word.toLowerCase().includes(query)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          // Đổi 'readingMatch' thành 'pronunciationMatch'
          const pronunciationMatch = item.pronunciation && item.pronunciation.toLowerCase().includes(query)
          const typeMatch = item.type && item.type.toLowerCase().includes(query)
          return wordMatch || meaningMatch || pronunciationMatch || typeMatch // Cập nhật biến tìm kiếm
        }).length
      }
      return vocabularyData.value.length
    })

    const filteredGrammar = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allGrammarData.value.filter(item => {
          const structureMatch = item.structure && item.structure.toLowerCase().includes(query)
          const explanationMatch = item.explanation && item.explanation.toLowerCase().includes(query)
          const exampleMatch = item.example && item.example.toLowerCase().includes(query)
          // Loại bỏ biến tìm kiếm 'pronunciationMatch' (không cần thiết cho ngữ pháp Tiếng Anh)
          // const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          const typeMatch = item.grammarType && item.grammarType.toLowerCase().includes(query)
          
          // Cập nhật logic trả về
          return structureMatch || explanationMatch || exampleMatch || typeMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return grammarData.value
    })

    const filteredGrammarTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allGrammarData.value.filter(item => {
          const structureMatch = item.structure && item.structure.toLowerCase().includes(query)
          const explanationMatch = item.explanation && item.explanation.toLowerCase().includes(query)
          const exampleMatch = item.example && item.example.toLowerCase().includes(query)
          // Loại bỏ biến tìm kiếm 'pronunciationMatch'
          // const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          const typeMatch = item.grammarType && item.grammarType.toLowerCase().includes(query)
          // Cập nhật logic trả về
          return structureMatch || explanationMatch || exampleMatch || typeMatch
        }).length
      }
      return grammarData.value.length
    })

    // Đổi tên 'filteredKanji' thành 'filteredIdioms'
    const filteredIdioms = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        // Đổi 'allKanjiData' thành 'allIdiomsData'
        const filtered = allIdiomsData.value.filter(item => {
          // Đổi 'characterMatch' thành 'phraseMatch'
          const phraseMatch = item.phrase && item.phrase.toLowerCase().includes(query)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          // Loại bỏ 'pronunciationMatch' và 'strokesMatch'
          // const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          // const strokesMatch = item.strokes && item.strokes.toString().includes(query)
          
          // Cập nhật logic trả về
          return phraseMatch || meaningMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      // Đổi 'kanjiData' thành 'idiomsData'
      return idiomsData.value
    })

    // Đổi tên 'filteredKanjiTotal' thành 'filteredIdiomsTotal'
    const filteredIdiomsTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        // Đổi 'allKanjiData' thành 'allIdiomsData'
        return allIdiomsData.value.filter(item => {
          // Đổi 'characterMatch' thành 'phraseMatch'
          const phraseMatch = item.phrase && item.phrase.toLowerCase().includes(query)
          const meaningMatch = item.meaning && item.meaning.toLowerCase().includes(query)
          // Loại bỏ 'pronunciationMatch' và 'strokesMatch'
          // const pronunciationMatch = item.vietnamesePronunciation && item.vietnamesePronunciation.toLowerCase().includes(query)
          // const strokesMatch = item.strokes && item.strokes.toString().includes(query)
          // Cập nhật logic trả về
          return phraseMatch || meaningMatch
        }).length
      }
      // Đổi 'kanjiData' thành 'idiomsData'
      return idiomsData.value.length
    })

    const filteredSentences = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        const filtered = allSentencesData.value.filter(item => {
          // Đổi 'japaneseMatch' thành 'englishMatch'
          const englishMatch = item.english && item.english.toLowerCase().includes(query)
          const vietnameseMatch = item.vietnamese && item.vietnamese.toLowerCase().includes(query)
          
          // Cập nhật logic trả về
          return englishMatch || vietnameseMatch
        })
        
        // Phân trang cho kết quả tìm kiếm
        const startIndex = searchCurrentPage.value * searchPageSize.value
        const endIndex = startIndex + searchPageSize.value
        return filtered.slice(startIndex, endIndex)
      }
      
      return sentencesData.value
    })

    const filteredSentencesTotal = computed(() => {
      if (searchQuery.value && searchQuery.value.trim()) {
        const query = searchQuery.value.trim().toLowerCase()
        return allSentencesData.value.filter(item => {
          // Đổi 'japaneseMatch' thành 'englishMatch'
          const englishMatch = item.english && item.english.toLowerCase().includes(query)
          const vietnameseMatch = item.vietnamese && item.vietnamese.toLowerCase().includes(query)
          // Cập nhật logic trả về
          return englishMatch || vietnameseMatch
        }).length
      }
      return sentencesData.value.length
    })

    // Methods
    const getTabCount = (tabKey) => {
      switch (tabKey) {
        case 'vocabulary': return filteredVocabularyTotal.value
        case 'grammar': return filteredGrammarTotal.value
        // Đổi 'filteredKanjiTotal' thành 'filteredIdiomsTotal'
        case 'idioms': return filteredIdiomsTotal.value
        case 'sentences': return filteredSentencesTotal.value
        default: return 0
      }
    }

    const getSearchPlaceholder = () => {
      switch (activeTab.value) {
        // Đổi 'cách đọc' thành 'phiên âm'
        case 'vocabulary': return 'Tìm kiếm theo từ, nghĩa, phiên âm hoặc loại từ...'
        case 'grammar': return 'Tìm kiếm theo cấu trúc, giải thích, ví dụ...'
        // Thay thế tìm kiếm 'ký tự, nghĩa, phát âm' bằng 'cụm từ, thành ngữ, nghĩa'
        case 'idioms': return 'Tìm kiếm theo cụm từ, thành ngữ hoặc nghĩa...'
        // Loại bỏ 'tiếng Nhật'
        case 'sentences': return 'Tìm kiếm theo câu hoặc nghĩa tiếng Việt...'
        default: return 'Tìm kiếm...'
      }
    }

    const getCreateButtonText = () => {
      switch (activeTab.value) {
        case 'vocabulary': return 'từ vựng'
        case 'grammar': return 'ngữ pháp'
        // Đổi 'hán từ' thành 'thành ngữ/cụm từ'
        case 'idioms': return 'thành ngữ/cụm từ'
        case 'sentences': return 'mẫu câu'
        default: return 'mục'
      }
    }

    const getModalTitle = () => {
      switch (activeTab.value) {
        case 'vocabulary': return 'từ vựng'
        case 'grammar': return 'ngữ pháp'
        // Đổi 'hán từ' thành 'thành ngữ/cụm từ'
        case 'idioms': return 'thành ngữ/cụm từ'
        case 'sentences': return 'mẫu câu'
        default: return 'mục'
      }
    }

    const getGrammarTypeName = (type) => {
      const types = {
        negative: 'Phủ định',
        positive: 'Khẳng định',
        progressive: 'Tiến hành',
        request: 'Yêu cầu',
        sequence: 'Trình tự',
        condition: 'Điều kiện',
        question: 'Nghi vấn',
        contrast: 'Tương phản'
      }
      return types[type] || type
    }

    // Utility functions
    const formatDate = (dateString) => {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('vi-VN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }

    // Đổi tên hàm từ 'loadAvailableKanjis' thành 'loadAvailableIdioms'
    const loadAvailableIdioms = async () => {
      try {
          // Gọi API getAll, sử dụng page=0 và size lớn để lấy toàn bộ (hoặc phần lớn) dữ liệu
          const response = await idiomsApi.getAll({ page: 0, size: 1000 }) 
          
          // Giả định API trả về dữ liệu danh sách nằm trong 'content' (chuẩn Spring Page)
          availableIdioms.value = response.content.map(item => ({
              id: item.idiomId,
              
              // Ánh xạ english_phrase sang phrase
              phrase: item.englishPhrase, 
              
              // Ánh xạ vietnamese_meaning sang meaning
              meaning: item.vietnameseMeaning 
          }))
          
      } catch (err) {
          console.error('Lỗi khi tải Thành ngữ/Cụm từ có sẵn:', err)
          availableIdioms.value = [] // Đặt lại thành mảng rỗng nếu có lỗi
      }
  }

    // Methods
    const openCreateModal = async () => {
      modalMode.value = 'create'
      
      if (activeTab.value === 'idioms') {
        // Khởi tạo trường dữ liệu cho 'Idioms'
        editingItem.value = {
          phrase: '', // Thay 'character' bằng 'phrase'
          meaning: '',
          category: '', // Có thể thêm 'category' thay cho 'strokes'
          // Loại bỏ 'strokes' và 'vietnamesePronunciation'
        }
      } else if (activeTab.value === 'grammar') {
        editingItem.value = {
          structure: '',
          explanation: '',
          example: '',
          grammarType: '',
          // Loại bỏ 'vietnamesePronunciation'
        }
      } else if (activeTab.value === 'sentences') {
        editingItem.value = {
          english: '', // Thay 'japanese' bằng 'english'
          vietnamese: ''
        }
      } else {
        // Vocabulary form - load available idioms
        await loadAvailableIdioms() // Đổi tên hàm
        editingItem.value = {
          words: '',               // item.words (được dùng trong form)
          phoneticIpa: '',         // item.phoneticIpa (trước là pronunciation)
          vietnameseMeaning: '',   // item.vietnameseMeaning (trước là meaning)
          wordType: '',            // item.wordType (trước là type)
          level: '',               // Trường mới
          synonyms: '',            // Trường mới
          antonyms: '',            // Trường mới
        }
      }
      
      showModal.value = true
    }

    const editItem = async (item) => {
      modalMode.value = 'edit'
      editingItem.value = { ...item }
      
      // Load available idioms if editing vocabulary
      if (activeTab.value === 'vocabulary') {
        await loadAvailableIdioms() // Đổi tên hàm
      }
      
      showModal.value = true
    }

    const deleteItem = (id) => {
      deleteItemId.value = id
      const itemType = getModalTitle().toLowerCase()
      deleteConfirmMessage.value = `Bạn có chắc chắn muốn xóa ${itemType} này không?\n\nHành động này không thể hoàn tác.`
      showDeleteConfirm.value = true
    }

    const confirmDelete = async () => {
      try {
        const id = deleteItemId.value
        let result
        
        if (activeTab.value === 'idioms') {
          // Đổi 'kanjiApi' thành 'idiomsApi' và 'loadKanji' thành 'loadIdioms'
          result = await idiomsApi.delete(id)
          await loadIdioms(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allIdiomsData.value.length > 0) { // Đổi 'allKanjiData' thành 'allIdiomsData'
            await loadAllIdioms() // Đổi tên hàm
          }
        } else if (activeTab.value === 'grammar') {
          result = await grammarApi.delete(id)
          await loadGrammar(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allGrammarData.value.length > 0) {
            await loadAllGrammar()
          }
        } else if (activeTab.value === 'sentences') {
          result = await sampleSentenceApi.delete(id)
          await loadSentences(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allSentencesData.value.length > 0) {
            await loadAllSentences()
          }
        } else {
          result = await vocabularyApi.delete(id)
          await loadVocabulary(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allVocabularyData.value.length > 0) {
            await loadAllVocabulary()
          }
        }
        
        // Kiểm tra kết quả và hiển thị thông báo thành công
        if (result === 1 || result) {
          // Xóa error message nếu có và hiển thị success message
          error.value = ''
          successMessage.value = `${getModalTitle()} đã được xóa thành công`
          
          // Tự động ẩn success message sau 3 giây
          setTimeout(() => {
            successMessage.value = ''
          }, 3000)
        }
      } catch (err) {
        error.value = `Không thể xóa ${getModalTitle()}`
        console.error(`Lỗi xóa ${getModalTitle().toLowerCase()}:`, err) // Cập nhật thông báo lỗi console
      } finally {
        showDeleteConfirm.value = false
        deleteItemId.value = null
      }
    }

    const cancelDelete = () => {
      showDeleteConfirm.value = false
      deleteItemId.value = null
    }

    const closeModal = () => {
      showModal.value = false
      modalMode.value = 'create'
      editingItem.value = null
      error.value = '' // Clear any error messages
    }

    const saveItem = async () => {
      try {
        const item = editingItem.value
        if (!item) return
        
        if (activeTab.value === 'idioms') {
          // Prepare idioms data for API
          
          const idiomData = {
            phraseText: item.englishPhrase, 
            meaning: item.vietnameseMeaning,
            category: item.category, 
            audioUrl: item.audioUrl || null,
            origin: item.origin || null,
            
          }
          

          if (modalMode.value === 'create') {
            await idiomsApi.create(idiomData)
          } else {
            await idiomsApi.update(item.id, idiomData)
          }
          await loadIdioms(currentPage.value) // Đổi tên hàm
          // Cập nhật allData nếu đã tải
          if (allIdiomsData.value.length > 0) { // Đổi tên biến
            await loadAllIdioms() // Đổi tên hàm
          }
        } else if (activeTab.value === 'grammar') {
          // Prepare grammar data for API
          const grammarData = {
            structure: item.structure,
            explanation: item.explanation,
            example: item.example,
            grammarType: item.grammarType,
            // Loại bỏ 'vietnamesePronunciation'
          }
          
          if (modalMode.value === 'create') {
            await grammarApi.create(grammarData)
          } else {
            await grammarApi.update(item.id, grammarData)
          }
          await loadGrammar(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allGrammarData.value.length > 0) {
            await loadAllGrammar()
          }
        } else if (activeTab.value === 'sentences') {
          // Prepare sentence data for API
          const sentenceData = {
            englishSentence: item.englishSentence, 
            vietnameseTranslation: item.vietnameseTranslation
          }
          
          if (modalMode.value === 'create') {
            await sampleSentenceApi.create(sentenceData)
          } else {
            await sampleSentenceApi.update(item.id, sentenceData)
          }
          await loadSentences(currentPage.value)
          // Cập nhật allData nếu đã tải
          if (allSentencesData.value.length > 0) {
            await loadAllSentences()
          }
        } else {
          // Validate vocabulary data
          if (!item.words || !item.vietnameseMeaning) {
            error.value = 'Vui lòng nhập đầy đủ từ vựng và nghĩa'
            return
          }
          
          // Prepare vocabulary data for API
          const vocabularyData = {
            words: item.words ? item.words.trim() : '',
            
            // Cập nhật tên trường chính xác
            vietnameseMeaning: item.vietnameseMeaning ? item.vietnameseMeaning.trim() : '',
            phoneticIpa: item.phoneticIpa ? item.phoneticIpa.trim() : '',
            wordType: item.wordType || 'noun',
            audioUrl: item.audioUrl || null,
            level: item.level || null,
            synonyms: item.synonyms || null,
            antonyms: item.antonyms || null,
          }
          
          if (modalMode.value === 'create') {
            const result = await vocabularyApi.create(vocabularyData)
            successMessage.value = 'Từ vựng đã được thêm thành công'
          } else {
            const result = await vocabularyApi.update(item.id, vocabularyData)
            successMessage.value = 'Từ vựng đã được cập nhật thành công'
          }
          await loadVocabulary(currentPage.value)
          
          // Cập nhật allData nếu đã tải
          if (allVocabularyData.value.length > 0) {
            await loadAllVocabulary()
          }
          
          // Auto hide success message after 3 seconds
          setTimeout(() => {
            successMessage.value = ''
          }, 3000)
        }
        
        closeModal()
      } catch (err) {
        error.value = `Không thể lưu ${getModalTitle()}`
        console.error(`Lỗi lưu ${getModalTitle().toLowerCase()}:`, err) // Cập nhật thông báo lỗi console
      }
    }

    // Search pagination functions
    // Search pagination functions
    const getSearchTotalPages = () => {
      let total = 0
      if (activeTab.value === 'vocabulary') {
        total = filteredVocabularyTotal.value
      } else if (activeTab.value === 'grammar') {
        total = filteredGrammarTotal.value
      } else if (activeTab.value === 'idioms') {
        // Đổi 'filteredKanjiTotal' thành 'filteredIdiomsTotal'
        total = filteredIdiomsTotal.value
      } else if (activeTab.value === 'sentences') {
        total = filteredSentencesTotal.value
      }
      return Math.ceil(total / searchPageSize.value)
    }

    const goToSearchPage = (page) => {
      searchCurrentPage.value = page
    }

    const nextSearchPage = () => {
      if (searchCurrentPage.value < getSearchTotalPages() - 1) {
        searchCurrentPage.value++
      }
    }

    const prevSearchPage = () => {
      if (searchCurrentPage.value > 0) {
        searchCurrentPage.value--
      }
    }

    // Watch search query để tải tất cả dữ liệu khi cần
    watch(searchQuery, async (newQuery, oldQuery) => {
      // Reset search pagination khi thay đổi search query
      searchCurrentPage.value = 0
      
      // Khi bắt đầu tìm kiếm (từ rỗng sang có nội dung)
      if (newQuery && newQuery.trim() && (!oldQuery || !oldQuery.trim())) {
        if (activeTab.value === 'vocabulary' && allVocabularyData.value.length === 0) {
          await loadAllVocabulary()
        } else if (activeTab.value === 'grammar' && allGrammarData.value.length === 0) {
          await loadAllGrammar()
        } else if (activeTab.value === 'idioms' && allIdiomsData.value.length === 0) { // Đổi 'allKanjiData' thành 'allIdiomsData'
          await loadAllIdioms() // Đổi 'loadAllKanji' thành 'loadAllIdioms'
        } else if (activeTab.value === 'sentences' && allSentencesData.value.length === 0) {
          await loadAllSentences()
        }
      }
    })

    // Client-side search - không cần API search nữa vì đã có computed properties để filter

    // Switch tab
    const switchTab = (tab) => {
      activeTab.value = tab
      currentPage.value = 0
      searchQuery.value = ''
      
      if (tab === 'vocabulary') {
        loadVocabulary()
      } else if (tab === 'idioms') {
        loadIdioms() // Đổi 'loadKanji' thành 'loadIdioms'
      } else if (tab === 'grammar') {
        loadGrammar()
      } else if (tab === 'sentences') {
        loadSentences()
      }
    }

    // Watchers
    watch(activeTab, () => {
      currentPage.value = 0 // Reset to first page when changing tabs
      if (activeTab.value === 'vocabulary') {
        loadVocabulary(0)
      } else if (activeTab.value === 'idioms') {
        loadIdioms(0) // Đổi 'loadKanji' thành 'loadIdioms'
      } else if (activeTab.value === 'grammar') {
        loadGrammar(0)
      } else if (activeTab.value === 'sentences') {
        loadSentences(0)
      }
    })

    // Load data on component mount
    onMounted(() => {
      loadVocabulary()
    })
</script>

<style src="./DictionaryManagement.scss" scoped></style>