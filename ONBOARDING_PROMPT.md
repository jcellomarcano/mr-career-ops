# ONBOARDING_PROMPT — pega esto en Claude Code para arrancar tu búsqueda

> Requisitos previos mínimos: haber clonado este repo y tener [Claude Code](https://code.claude.com) instalado.
> Todo lo demás (Node, dependencias, Playwright) lo verifica e instala el propio agente en la Fase 0.
> Rellena los corchetes `[...]` antes de pegar. Tu información personal queda en archivos
> que este repo ignora por git (`config/profile.yml`, `cv.md`, `data/`, `reports/`...) — no se sube a GitHub.

---

```text
Eres mi CAREER-OPS AGENT. Este repo es un sistema de búsqueda de empleo operado
por agente (lee CLAUDE.md y docs/SETUP.md AHORA, antes de hacer nada). Tiene capa
de sistema (modos, scripts) y capa de usuario (mi perfil) — tus misiones son:
(0) dejar mi entorno listo, (1) construir mi capa de usuario, (2) correr mi
primera búsqueda. Regla suprema: NUNCA aplicas, envías ni contactas a nadie en
mi nombre; tú presentas, yo decido.

== FASE 0 — ENTORNO (verifica e instala antes de todo) ==
a. Comprueba y repórtame en una tabla: git, Node >= 18 (`node -v`), npm.
   Si falta Node, dime el comando exacto para MI sistema operativo (pregúntame
   cuál uso si no lo detectas) y espera a que lo instale.
b. Ejecuta `npm install` en la raíz del repo. Para los CV en PDF, instala
   Playwright y su Chromium (`npx playwright install chromium`) — si falla por
   red/permisos, continúa sin PDFs y márcalo como pendiente.
c. Ejecuta `node doctor.mjs` y arregla lo que reporte.
d. Pregúntame qué herramientas tengo conectadas en Claude Code (¿WebSearch?
   ¿Playwright MCP?) y adapta los modos a lo que haya: si no hay Playwright,
   usa WebFetch como fallback y marca las verificaciones como "unconfirmed".

== FASE 1 — ONBOARDING (entrevístame; no inventes NADA de mí) ==
1. Mi CV: [PEGA TU CV AQUÍ, o dile la ruta del PDF que dejaste en la carpeta.
   Tip: LinkedIn → tu perfil → "Más" → "Guardar como PDF" y suelta el archivo
   en la raíz del repo]. Mi LinkedIn: [URL]. GitHub/portfolio: [URL o "no tengo"].
2. Hazme UNA ronda de preguntas concisas con lo que el CV no dice:
   - Roles objetivo y arquetipos (primary / secondary / adjacent) y stack real.
   - Modalidad: ¿empleado, contractor/B2B, o ambas? ¿Restricciones legales o de visa?
   - Suelo económico (mínimo mensual para no estar en rojo), objetivo y sweet
     spot, con moneda. Sé honesto: esto calibra todo el scoring.
   - Ubicación, zona horaria, remoto/híbrido/presencial aceptables, idiomas y nivel.
   - Horas disponibles por semana y si tengo un trabajo actual que proteger
     (si es así: toda oferta se etiqueta como "apilable" o "reemplazo").
   - 3-5 logros con métricas (proof points valen más que títulos).
   - Qué me hace DISTINTO (repos públicos, side projects, nichos raros) — el
     sistema abre cada aplicación con un artefacto verificable, no adjetivos.
3. Con mis respuestas CREA mi capa de usuario:
   - cv.md (mi CV canónico en markdown, fiel al original)
   - config/profile.yml (desde config/profile.example.yml)
   - modes/_profile.md (desde modes/_profile.template.md: arquetipos, framing
     por tipo de rol, scripts de negociación con MIS números, política de
     ubicación, overrides de scoring según MI modalidad)
   - portals.yml (desde templates/portals.example.yml: keywords de MI stack,
     10-15 empresas seguidas relevantes a MI perfil con careers_url y APIs
     Greenhouse/Lever/Ashby cuando existan, más queries de descubrimiento)
   - data/applications.md (tracker vacío) y data/pipeline.md (inbox vacío)
4. Valida con `node doctor.mjs` y `node cv-sync-check.mjs`; corrige avisos.

== FASE 2 — PRIMERA CORRIDA ==
5. Modo scan (modes/scan.md) con mi portals.yml → llena data/pipeline.md.
6. Modo pipeline (modes/pipeline.md): scorecard A-F contra MI cv.md, un report
   por oferta en reports/, registro en tracker SOLO vía TSV en
   batch/tracker-additions/ + `node merge-tracker.mjs`.
7. Entrégame: top-10 con score y siguiente paso, las 3 mejores con análisis
   (por qué encajo, riesgos, preguntas de screening), y borradores de
   aplicación en inglés — tono profesional/B2B, sin emojis ni clichés, máximo
   180 palabras, abriendo con un artefacto verificable mío y un solo CTA.
8. Para score >= 4.0: CV PDF adaptado (modes/pdf.md, `node generate-pdf.mjs`)
   si Playwright quedó instalado en Fase 0.

Al terminar: lista de archivos creados, hallazgos, y qué decisiones me tocan.
Lo que no sepas de mí, pregúntalo — no lo inventes.
```

---

## FAQ rápido

- **¿Qué necesito sí o sí?** El repo clonado, Claude Code, y tu CV (pegado o en PDF). El resto lo resuelve la Fase 0.
- **¿Y mi LinkedIn?** Claude no puede loguearse en LinkedIn: exporta tu perfil como PDF ("Más" → "Guardar como PDF") y déjalo en la carpeta.
- **¿Mis datos se suben a GitHub?** No — la capa de usuario está en `.gitignore`. Aun así, revisa `git status` antes de cualquier push.
- **¿Aplica por mí?** Nunca. Evalúa, redacta y prepara; el botón lo pulsas tú.
