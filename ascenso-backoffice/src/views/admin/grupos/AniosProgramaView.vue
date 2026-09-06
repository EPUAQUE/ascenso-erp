<script setup lang="ts">
import AniosProgramaTab from '@/components/catalogo/AniosProgramaTab.vue'

defineProps<{ grupoId: number }>()
</script>

<template>
  <AniosProgramaTab :grupo-id="grupoId" />
</template>
