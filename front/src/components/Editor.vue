<template>
  <div class="main-container">
    <div
      class="editor-container editor-container_classic-editor"
      ref="editorContainerElement"
    >
      <div class="editor-container__editor">
        <div ref="editorElement" :class="{ write: isModified }">
          <Ckeditor
            v-if="isLayoutReady"
            v-model="currentContent"
            :editor="editor"
            :config="editorConfig"
            :disabled="isModified"
            class="text-xs text-gray-400 font-light self-center"
          />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed, defineProps, defineEmits } from "vue";
import { Ckeditor } from "@ckeditor/ckeditor5-vue";
import {
  ClassicEditor,
  FontSize,
  FontColor,
  FontFamily,
  AccessibilityHelp,
  Alignment,
  AutoImage,
  Autosave,
  Base64UploadAdapter,
  Bold,
  Essentials,
  Heading,
  Highlight,
  Italic,
  Link,
  LinkImage,
  List,
  ListProperties,
  Paragraph,
  PasteFromOffice,
  SelectAll,
  Strikethrough,
  Table,
  TableCaption,
  TableCellProperties,
  TableColumnResize,
  TableProperties,
  TableToolbar,
  TodoList,
  Underline,
  Undo,
} from "ckeditor5";

import translations from "ckeditor5/translations/ko.js";
import "ckeditor5/ckeditor5.css";

// 부모로부터 받는 값 (v-model 연결용)
const props = defineProps({
  modelValue: {
    type: String,
    default: "",
  },
  isRead: {
    type: Boolean,
    default: "",
  },
});

const currentContent = computed({
  get: () => props.modelValue,
  set: (value) => emit("update:currentPage", value),
});

const isModified = computed({
  get: () => props.isRead,
});

const emit = defineEmits(["update:currentPage"]);

const isLayoutReady = ref(false);
const editor = ClassicEditor;

const editorConfig = computed(() => {
  return {
    licenseKey: "GPL",
    fontFamily: {
      options: [
        "default",
        "Ubuntu, Arial, sans-serif",
        "Ubuntu Mono, Courier New, Courier, monospace",
      ],
    },
    fontSize: {
      options: [9, 11, 13, "default", 17, 19, 21],
    },
    toolbar: {
      items: isModified.value
        ? []
        : [
            "fontSize",
            "fontFamily",
            "fontColor",
            "fontBackgroundColor",
            "undo",
            "redo",
            "|",
            "selectAll",
            "|",
            "heading",
            "|",
            "bold",
            "italic",
            "underline",
            "strikethrough",
            "|",
            "link",

            "insertTable",
            "highlight",
            "|",
            "alignment",
            "|",
            "bulletedList",
            "numberedList",
            "todoList",
            "|",
            "accessibilityHelp",
          ],
    },
    plugins: [
      FontSize,
      FontFamily,
      AccessibilityHelp,
      Alignment,
      Autosave,
      Base64UploadAdapter,
      Bold,
      Essentials,
      Heading,
      Highlight,

      Italic,
      Link,
      LinkImage,
      List,
      ListProperties,
      Paragraph,
      PasteFromOffice,
      SelectAll,
      Strikethrough,
      Table,
      TableCaption,
      TableCellProperties,
      TableColumnResize,
      TableProperties,
      TableToolbar,
      TodoList,
      Underline,
      Undo,
    ],
    heading: {
      options: [
        {
          model: "paragraph",
          title: "Paragraph",
          class: "ck-heading_paragraph",
        },
        {
          model: "heading1",
          view: "h1",
          title: "Heading 1",
          class: "ck-heading_heading1",
        },
        {
          model: "heading2",
          view: "h2",
          title: "Heading 2",
          class: "ck-heading_heading2",
        },
        {
          model: "heading3",
          view: "h3",
          title: "Heading 3",
          class: "ck-heading_heading3",
        },
        {
          model: "heading4",
          view: "h4",
          title: "Heading 4",
          class: "ck-heading_heading4",
        },
        {
          model: "heading5",
          view: "h5",
          title: "Heading 5",
          class: "ck-heading_heading5",
        },
        {
          model: "heading6",
          view: "h6",
          title: "Heading 6",
          class: "ck-heading_heading6",
        },
      ],
    },

    list: {
      properties: {
        styles: true,
        startIndex: true,
        reversed: true,
      },
    },

    initialData: "",
    language: "ko",
    translations: [translations],
  };
});

onMounted(() => {
  isLayoutReady.value = true;
});
</script>

<style scoped>
/* ── 에디터 전체 width 문제 해결 ── */
:deep(.ck.ck-editor),
:deep(.ck-editor__main),
:deep(.ck-editor__editable) {
  width: 100% !important;
  box-sizing: border-box !important;
}

/* ── 리스트 (번호/불릿) 여백 ── */
:deep(.ck-content ul),
:deep(.ck-content ol) {
  padding-left: 24px !important;
  margin-left: 0;
}

/* ── Heading 스타일 ── */
:deep(.ck-content h1) {
  font-size: 1.8em;
  font-weight: 700;
  margin-top: 1em;
}
:deep(.ck-content h2) {
  font-size: 1.5em;
  font-weight: 700;
  margin-top: 0.8em;
}
:deep(.ck-content h3) {
  font-size: 1.3em;
  font-weight: 600;
  margin-top: 0.6em;
}
:deep(.ck-content h4) {
  font-size: 1.1em;
  font-weight: 600;
}

/* ── 본문 텍스트 ── */
:deep(.ck-content p) {
  font-size: 14px;
  line-height: 1.7;
  color: #374151;
  margin: 0 0 6px 0;
}

/* ── 기본 에디터 스타일 (작성 모드) ── */
:deep(.ck.ck-editor__main > .ck-editor__editable) {
  height: 300px;
  border: 1px solid #e2e8f0;
  background: #f8fafc;
  padding: 12px;
  font-size: 14px;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
}

/* 포커스 */
:deep(.ck.ck-editor__main > .ck-editor__editable:focus) {
  border-color: #94a3b8;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15);
  background: #fff;
}

:deep(.write .ck.ck-editor__main > .ck-editor__editable) {
  border: none !important;
  background: transparent !important;
  padding: 0 !important;
  min-height: auto !important;
  box-shadow: none !important;
}

/* 툴바 제거 */
:deep(.write .ck-editor__top) {
  display: none !important;
}

/* ── 테이블 스타일 ── */
:deep(.ck-content table) {
  border-collapse: collapse;
  width: 100%;
  margin: 10px 0;
}

:deep(.ck-content td),
:deep(.ck-content th) {
  border: 1px solid #e5e7eb;
  padding: 8px;
  font-size: 13px;
}

/* ── 링크 스타일 ── */
:deep(.ck-content a) {
  color: #2563eb;
  text-decoration: underline;
}

/* ── blockquote ── */
:deep(.ck-content blockquote) {
  border-left: 4px solid #e5e7eb;
  padding-left: 12px;
  color: #6b7280;
  margin: 10px 0;
}

/* ── 코드 블럭 ── */
:deep(.ck-content pre) {
  background: #1e293b;
  color: #f1f5f9;
  padding: 12px;
  border-radius: 8px;
  font-size: 13px;
  overflow-x: auto;
}
</style>
