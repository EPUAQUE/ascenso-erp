export interface Nino {
  id: number
  destacamentoId: number
  nombreCompleto: string
  fechaNacimiento: string
  fotoUrl: string | null
  encargadoNombre: string
  encargadoContacto: string
  contactoEmergencia: string | null
  fechaIngreso: string
  grupoActualId: number
  anioProgramaActualId: number
  activo: boolean
}

export interface CrearNinoRequest {
  nombreCompleto: string
  fechaNacimiento: string
  fotoUrl: string | null
  encargadoNombre: string
  encargadoContacto: string
  contactoEmergencia: string | null
  fechaIngreso: string
  grupoActualId: number
  anioProgramaActualId: number
}

export interface ActualizarNinoRequest {
  nombreCompleto: string
  fechaNacimiento: string
  fotoUrl: string | null
  encargadoNombre: string
  encargadoContacto: string
  contactoEmergencia: string | null
}

export interface PromoverNinoRequest {
  grupoId: number
  anioProgramaId: number
}

export interface Trimestre {
  id: number
  anioCalendario: number
  numero: number
  fechaInicio: string
  fechaFin: string
}

export interface GuardarTrimestreRequest {
  anioCalendario: number
  numero: number
  fechaInicio: string
  fechaFin: string
}

export interface Asistencia {
  id: number
  ninoId: number
  trimestreId: number
  fecha: string
  presente: boolean
  registradoPor: number
}

export interface RegistrarAsistenciaRequest {
  trimestreId: number
  fecha: string
  presente: boolean
}

export interface CorregirAsistenciaRequest {
  presente: boolean
}

export interface ProgresoLibro {
  id: number
  ninoId: number
  libroBiblicoId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
  registradoPor: number
}

export interface RegistrarProgresoLibroRequest {
  libroBiblicoId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
}

export interface ProgresoDestreza {
  id: number
  ninoId: number
  destrezaId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
  registradoPor: number
}

export interface RegistrarProgresoDestrezaRequest {
  destrezaId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
}

export interface ProgresoLiderazgo {
  id: number
  ninoId: number
  liderazgoId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
  registradoPor: number
}

export interface RegistrarProgresoLiderazgoRequest {
  liderazgoId: number
  anioProgramaObjetivoId: number
  fechaCompletado: string
}

export interface ProgresoRequisito {
  id: number
  ninoId: number
  pasoRequeridoId: number
  fechaCompletado: string
  registradoPor: number
}

export interface RegistrarProgresoRequisitoRequest {
  pasoRequeridoId: number
  fechaCompletado: string
}

export interface MedallaOtorgada {
  id: number
  ninoId: number
  anioProgramaId: number
  fechaOtorgada: string
}

export interface OtorgarMedallaRequest {
  anioProgramaId: number
  fechaOtorgada: string
}

export interface LogroMayor {
  id: number
  ninoId: number
  fechaOtorgada: string
}

export interface OtorgarLogroMayorRequest {
  fechaOtorgada: string
}
