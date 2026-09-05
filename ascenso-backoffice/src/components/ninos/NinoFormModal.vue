<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import { ApiClientError } from '@/services/http/ApiClient'
import type { AnioPrograma, Grupo } from '@/types/catalogo'
import type { ActualizarNinoRequest, CrearNinoRequest, Nino } from '@/types/ninos'

const props = defineProps<{
  modelValue: boolean
  nino: Nino | null
  grupos: Grupo[]
  aniosPrograma: AnioPrograma[]
  crear: (request: CrearNinoRequest) => Promise<Nino>
  actualizar: (id: number, request: ActualizarNinoRequest) => Promise<Nino>
}>()

const emit = defineEmits<{ (e: 'update:modelValue', value: boolean): void }>()

const esEdicion = computed(() => props.nino !== null)

const nombreCompleto = ref('')
const fechaNacimiento = ref('')
const fotoUrl = ref('')
const encargadoNombre = ref('')
const encargadoContacto = ref('')
const contactoEmergencia = ref('')
const fechaIngreso = ref('')
const grupoActualId = ref<number | null>(null)
const anioProgramaActualId = ref<number | null>(null)

const guardando = ref(false)
const errorMessage = ref<string | null>(null)

const aniosDelGrupo = computed(() =>
  props.aniosPrograma.filter((a) => a.grupoId === grupoActualId.value).sort((a, b) => a.numero - b.numero),
)

function resetear() {
  const n = props.nino
  nombreCompleto.value = n?.nombreCompleto ?? ''
  fechaNacimiento.value = n?.fechaNacimiento ?? ''
  fotoUrl.value = n?.fotoUrl ?? ''
  encargadoNombre.value = n?.encargadoNombre ?? ''
  encargadoContacto.value = n?.encargadoContacto ?? ''
  contactoEmergencia.value = n?.contactoEmergencia ?? ''
  fechaIngreso.value = n?.fechaIngreso ?? ''
  grupoActualId.value = n?.grupoActualId ?? props.grupos[0]?.id ?? null
  anioProgramaActualId.value =
    n?.anioProgramaActualId ?? props.aniosPrograma.find((a) => a.grupoId === grupoActualId.value)?.id ?? null
  errorMessage.value = null
}

watch(
  () => props.modelValue,
  (abierto) => {
    if (abierto) resetear()
  },
)

// Cambiar de grupo en el formulario de creación invalida el año de programa
// elegido si pertenecía al grupo anterior — se limpia para forzar a elegir
// uno válido del nuevo grupo.
watch(grupoActualId, () => {
  if (esEdicion.value) return
  if (!aniosDelGrupo.value.some((a) => a.id === anioProgramaActualId.value)) {
    anioProgramaActualId.value = aniosDelGrupo.value[0]?.id ?? null
  }
})

function etiquetaAnio(anio: AnioPrograma): string {
  const medalla = anio.medalla ? ` — medalla ${anio.medalla}` : ''
  return `Año ${anio.numero}${medalla}${anio.esAnioGracia ? ' (gracia)' : ''}`
}

function aTextoONulo(valor: string): string | null {
  const v = valor.trim()
  return v ? v : null
}

async function onSubmit() {
  if (guardando.value) return
  errorMessage.value = null

  if (
    !nombreCompleto.value.trim() ||
    !fechaNacimiento.value ||
    !encargadoNombre.value.trim() ||
    !encargadoContacto.value.trim() ||
    (!esEdicion.value && (!fechaIngreso.value || !grupoActualId.value || !anioProgramaActualId.value))
  ) {
    errorMessage.value = 'Completa los campos requeridos.'
    return
  }

  guardando.value = true
  try {
    if (esEdicion.value && props.nino) {
      await props.actualizar(props.nino.id, {
        nombreCompleto: nombreCompleto.value.trim(),
        fechaNacimiento: fechaNacimiento.value,
        fotoUrl: aTextoONulo(fotoUrl.value),
        encargadoNombre: encargadoNombre.value.trim(),
        encargadoContacto: encargadoContacto.value.trim(),
        contactoEmergencia: aTextoONulo(contactoEmergencia.value),
      })
    } else {
      await props.crear({
        nombreCompleto: nombreCompleto.value.trim(),
        fechaNacimiento: fechaNacimiento.value,
        fotoUrl: aTextoONulo(fotoUrl.value),
        encargadoNombre: encargadoNombre.value.trim(),
        encargadoContacto: encargadoContacto.value.trim(),
        contactoEmergencia: aTextoONulo(contactoEmergencia.value),
        fechaIngreso: fechaIngreso.value,
        grupoActualId: grupoActualId.value as number,
        anioProgramaActualId: anioProgramaActualId.value as number,
      })
    }
    emit('update:modelValue', false)
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar el niño.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <ModalDialog
    :model-value="modelValue"
    :title="esEdicion ? 'Editar niño' : 'Nuevo niño'"
    max-width="max-w-2xl"
    @update:model-value="(v) => emit('update:modelValue', v)"
  >
    <form class="space-y-4" @submit.prevent="onSubmit">
      <p
        v-if="errorMessage"
        class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
        role="alert"
      >
        {{ errorMessage }}
      </p>

      <div class="grid grid-cols-1 gap-4 sm:grid-cols-2">
        <div class="sm:col-span-2">
          <label class="mb-1 block text-sm font-medium text-mk-text">Nombre completo</label>
          <input
            v-model="nombreCompleto"
            type="text"
            maxlength="150"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Fecha de nacimiento</label>
          <input
            v-model="fechaNacimiento"
            type="date"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Foto (URL, opcional)</label>
          <input
            v-model="fotoUrl"
            type="text"
            maxlength="255"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Nombre del encargado</label>
          <input
            v-model="encargadoNombre"
            type="text"
            maxlength="150"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Contacto del encargado</label>
          <input
            v-model="encargadoContacto"
            type="text"
            maxlength="50"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div class="sm:col-span-2">
          <label class="mb-1 block text-sm font-medium text-mk-text">Contacto de emergencia (opcional)</label>
          <input
            v-model="contactoEmergencia"
            type="text"
            maxlength="50"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <template v-if="!esEdicion">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Fecha de ingreso</label>
            <input
              v-model="fechaIngreso"
              type="date"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>

          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Grupo etario</label>
            <select
              v-model.number="grupoActualId"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            >
              <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
            </select>
          </div>

          <div class="sm:col-span-2">
            <label class="mb-1 block text-sm font-medium text-mk-text">Año de programa</label>
            <select
              v-model.number="anioProgramaActualId"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            >
              <option v-for="a in aniosDelGrupo" :key="a.id" :value="a.id">{{ etiquetaAnio(a) }}</option>
            </select>
          </div>
        </template>
      </div>

      <div class="flex justify-end gap-2 pt-2">
        <button type="button" class="mk-btn mk-btn-ghost" @click="emit('update:modelValue', false)">
          Cancelar
        </button>
        <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
          {{ guardando ? 'Guardando…' : 'Guardar' }}
        </button>
      </div>
    </form>
  </ModalDialog>
</template>
