<script setup lang="ts">
import LibrosBiblicosTab from '@/components/catalogo/LibrosBiblicosTab.vue'

defineProps<{ grupoId: number }>()
</script>

<template>
  <LibrosBiblicosTab :grupo-id="grupoId" />
</template>
