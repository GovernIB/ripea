import {Box, Icon, Typography} from '@mui/material';
import { BasePage } from 'reactlib';
import {CardPage} from "../components/CardData.tsx";

const Accesibilitat: React.FC = () => {
    return <BasePage>
        <CardPage header={<Typography variant="h3">Declaración de Accesibilidad (en proceso)</Typography>}>
            <Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3">Introducción</Typography>
                    El presente documento constituye la Declaración de Accesibilidad del sitio web [NOMBRE DEL SITIO], desarrollado con tecnología React y Material-UI. Esta declaración se elabora en cumplimiento del Real Decreto 1112/2018, de 7 de septiembre, sobre accesibilidad de los sitios web y aplicaciones para dispositivos móviles del sector público, y tiene como objetivo informar de forma transparente sobre el grado de conformidad con los criterios de accesibilidad reconocidos.
                </Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3" display={'flex'} alignItems={'center'}><Icon color={'success'} sx={{fontSize: 'inherit'}}>check_circle</Icon>Situación de cumplimiento</Typography>
                    Este sitio web se encuentra parcialmente conforme con el Real Decreto 1112/2018. Se han implementado medidas correctoras para la mayoría de los criterios de accesibilidad de los niveles A y AA de la norma UNE-EN 301549:2022, aunque persisten algunas incidencias técnicas que se detallan a continuación, junto con las soluciones aplicadas o en proceso de implementación.
                    <Box sx={{p:1}}>Criterio A - 4.1.2 Name, Role, Value: Algunos botones iconográficos carecían de alternativa textual accesible. Solución aplicada: se ha añadido el atributo "title" y el atributo "aria-label" con texto descriptivo a todos los botones que no disponían de etiqueta visible, garantizando que los lectores de pantalla puedan identificar su función.</Box>
                    <Box sx={{p:1}}>Criterio A - 1.1.1 Non-text Content: Algunas imágenes informativas no disponían de texto alternativo. Solución aplicada: se ha realizado una auditoría de todos los recursos gráficos. Las imágenes no decorativas incluyen ahora un atributo "alt" descriptivo y contextual. Las imágenes puramente decorativas utilizan "alt" vacío o "role=presentation" para ser ignoradas por las tecnologías de apoyo.</Box>
                    <Box sx={{p:1}}>Criterio AA - 1.4.4 Resize Text: El texto se truncaba al escalar la interfaz al 200%. Solución aplicada: en tamaños de pantalla reducidos, los botones muestran únicamente el icono acompañado de un atributo "title" descriptivo. Se ha eliminado el uso de "overflow: hidden" en contenedores de texto y se ha verificado que todo el contenido permanezca accesible con zoom del 200%.</Box>
                    <Box sx={{p:1}}>Criterio AA - 2.5.8 Target Size (Minimum): Determinados elementos interactivos no cumplían con el tamaño mínimo de 24x24 píxeles o el espaciado requerido. Solución aplicada: se ha modificado el posicionamiento y maquetación de los componentes para garantizar un área de pulsación adecuada y un espaciado mínimo de 8 píxeles entre elementos interactivos, facilitando su uso en dispositivos táctiles y por personas con dificultades de movilidad.</Box>
                </Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3" display={'flex'} alignItems={'center'}><Icon color={'error'} sx={{fontSize: 'inherit'}}>cancel</Icon>Lista de contenido no accesible y explicación del motivo</Typography>
                    <Box sx={{p:1}}>Criterio A - 2.5.3 Label in Name: En ciertos componentes interactivos derivados de librerías de terceros, el nombre accesible no coincidía con la etiqueta visible. Solución aplicada: se ha informado de esta incidencia al equipo mantenedor de la librería. Mientras se publica una corrección oficial, se aplica una sincronización manual entre el texto visible y el atributo "aria-label" en los componentes afectados.</Box>
                    <Box sx={{p:1}}>Criterio A - 3.1.1 Language of Page: El idioma principal de la página no se declara mediante el atributo "lang" en el elemento HTML raíz. Explicación: en nuestra arquitectura, la gestión del idioma se realiza a través de la sesión de usuario y el contexto de la aplicación React, no mediante atributos estáticos en el HTML. Como medida compensatoria, el atributo "lang" se inyecta dinámicamente en el contenedor principal según el idioma seleccionado por el usuario.</Box>
                    <Box sx={{p:1}}>Criterio AA - 1.4.3 Contrast (Minimum): Se detectó contraste insuficiente entre texto y fondo en algunos elementos de la interfaz. Solución aplicada: se ha revisado la paleta de colores corporativa para asegurar una relación de contraste mínima de 4.5:1 para texto normal y 3:1 para texto grande, conforme a los requisitos WCAG 2.1 nivel AA.</Box>
                </Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3" display={'flex'} alignItems={'center'}><Icon color={'warning'} sx={{fontSize: 'inherit'}}>calendar_today</Icon>Preparación de la presente declaración</Typography>
                    Esta declaración se ha elaborado mediante: autoevaluación realizada por el equipo de desarrollo utilizando herramientas automatizadas (axe-core, Lighthouse), revisión manual de componentes críticos, y pruebas con usuarios que emplean tecnologías de apoyo como lectores de pantalla (NVDA, VoiceOver) y navegación exclusiva por teclado. Fecha de preparación: [FECHA]. Última revisión: [FECHA]. Próxima revisión programada: [FECHA + 1 año]. Norma de referencia: UNE-EN 301549:2022, niveles A y AA.
                </Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3" display={'flex'} alignItems={'center'}><Icon color={'success'} sx={{fontSize: 'inherit'}}>mail</Icon>Observaciones y datos de contacto</Typography>
                    Si identifica alguna barrera de accesibilidad no recogida en esta declaración, o tiene dificultades para acceder a algún contenido, póngase en contacto con nosotros a través de:
                    <ul>
                        <li>Correo electrónico: accesibilidad@[dominio].es</li>
                        <li>Teléfono: [NÚMERO] (horario de atención: lunes a viernes, 9:00 a 18:00)</li>
                        <li>Formulario web: [URL del formulario]</li>
                        <li>Dirección postal: [DIRECCIÓN COMPLETA]</li>
                        <li>Todas las comunicaciones serán gestionadas por la Unidad de Accesibilidad Digital, en cumplimiento del artículo 10.2.a) del Real Decreto 1112/2018.</li>
                    </ul>
                </Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3" display={'flex'} alignItems={'center'}><Icon color={'error'} sx={{fontSize: 'inherit'}}>error</Icon>Procedimiento de aplicación</Typography>
                    En caso de recibir una comunicación sobre una posible incidencia de accesibilidad, se seguirá el siguiente procedimiento:
                    <ol>
                        <li>Acuse de recibo en un plazo máximo de 5 días hábiles.</li>
                        <li>Análisis técnico de la incidencia reportada y evaluación de su conformidad con la normativa.</li>
                        <li>Respuesta motivada en un plazo máximo de 20 días hábiles, indicando las medidas correctoras aplicables o, en su caso, los motivos de desestimación.</li>
                        <li>Si la respuesta no fuera satisfactoria, el interesado podrá presentar reclamación administrativa a través de los registros electrónicos o presenciales habilitados, conforme a la Ley 39/2015, de 1 de octubre, del Procedimiento Administrativo Común de las Administraciones Públicas.</li>
                    </ol>
                </Box>
                <Box sx={{p:1}}>
                    <Typography variant="h3" display={'flex'} alignItems={'center'}><Icon color={'warning'} sx={{fontSize: 'inherit'}}>add_circle</Icon>Contenido opcional</Typography>
                    <Box sx={{p:1}}>Medidas de accesibilidad adicionales implementadas: estructura semántica HTML5, navegación por teclado con indicador visual de foco, enlaces con texto significativo, formularios con etiquetas asociadas y mensajes de error accesibles, diseño responsive adaptable, y compatibilidad verificada con lectores de pantalla principales.</Box>
                    <Box sx={{p:1}}>Configuración técnica recomendada: navegadores actualizados (Chrome, Firefox, Edge, Safari en sus dos últimas versiones), resolución mínima de 1280x720 píxeles, y soporte de zoom hasta 200% sin pérdida de contenido o funcionalidad.</Box>
                    <Box sx={{p:1}}>Atajos de teclado disponibles: Alt+1 (saltar al contenido principal), Alt+2 (ir al menú de navegación), Alt+3 (acceder al buscador), Alt+0 (ver esta declaración de accesibilidad).</Box>
                    <Box sx={{p:1}}>Recursos de interés: Guía de accesibilidad web del W3C (https://www.w3.org/WAI/), validadores automáticos de accesibilidad (https://achecker.ca/), y documentación oficial del Real Decreto 1112/2018.</Box>
                </Box>
            </Box>
        </CardPage>
    </BasePage>;
}

export default Accesibilitat;