<script setup lang="ts">
import { liderazgoService } from '@/services/catalogo/LiderazgoService'
import NombreCategoriaTab from '@/components/catalogo/NombreCategoriaTab.vue'

defineProps<{ grupoId: number }>()
</script>

<template>
  <NombreCategoriaTab
    entidad-label="Liderazgo"
    campo-categoria-label="Categoría"
    :grupo-id="grupoId"
    :listar="liderazgoService.listar"
    :crear="liderazgoService.crear"
    :actualizar="liderazgoService.actualizar"
    :activar="liderazgoService.activar"
    :desactivar="liderazgoService.desactivar"
  />
</template>
