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
    menuBar: {
      isVisible: isModified.value ? false : true,
    },
    initialData: "",
    language: "ko",
    translations: [translations],
    placeholder: "내용을 입력해주세요.",
  };
});

onMounted(() => {
  isLayoutReady.value = true;
});
</script>

<style scoped>
/* 에디터 높이 조절 등 공통 스타일 */
:deep(.ck-editor__editable) {
  min-height: 300px;
}

/* 1. 에디터 내부 리스트(번호, 불렛)의 왼쪽 여백 확보 */
:deep(.ck-content ul),
:deep(.ck-content ol) {
  padding-left: 40px !important; /* 번호나 점이 보일 공간 확보 */
  margin-left: 0;
}

/* 에디터 내부의 제목(Heading) 스타일 강제 부여 */
:deep(.ck-content h1) {
  font-size: 2.1em !important;
  font-weight: bold !important;
  margin-top: 1.2em;
  display: block !important;
}
:deep(.ck-content h2) {
  font-size: 1.8em !important;
  font-weight: bold !important;
  margin-top: 1em;
  display: block !important;
}

:deep(.ck-content h3) {
  font-size: 1.5em !important;
  font-weight: bold !important;
  margin-top: 0.8em;
  display: block !important;
}

:deep(.ck-content h4) {
  font-size: 1.2em !important;
  font-weight: bold !important;
  display: block !important;
}

/* 문단(p) 스타일 */
:deep(.ck-content p) {
  font-size: 1em;
  line-height: 1.6;
}

:deep(.write .ck.ck-editor__main > .ck-editor__editable) {
  border-radius: 10px !important;
  border: 1px solid #e2e8f0 !important;
  background: #f8fafc !important;
  box-shadow: none !important;
  transition:
    border-color 0.2s,
    box-shadow 0.2s;
  font-size: 13px;
  padding: 12px !important;
}

/* :deep([data-v-9a196164] .ck.ck-editor__main > .ck-editor__editable:focus) {
  border-color: #94a3b8 !important;
  box-shadow: 0 0 0 3px rgba(148, 163, 184, 0.15) !important;
  background: #fff !important;
} */
</style>
