<script setup lang="ts">
import { destrezaService } from '@/services/catalogo/DestrezaService'
import NombreCategoriaTab from '@/components/catalogo/NombreCategoriaTab.vue'

defineProps<{ grupoId: number }>()
</script>

<template>
  <NombreCategoriaTab
    entidad-label="Destreza"
    campo-categoria-label="Categoría"
    :grupo-id="grupoId"
    :listar="destrezaService.listar"
    :crear="destrezaService.crear"
    :actualizar="destrezaService.actualizar"
    :activar="destrezaService.activar"
    :desactivar="destrezaService.desactivar"
  />
</template>
