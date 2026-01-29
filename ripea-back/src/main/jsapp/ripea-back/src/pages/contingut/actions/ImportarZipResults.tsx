import React from 'react';
import { Box, Typography, LinearProgress } from '@mui/material';
import { useTranslation } from 'react-i18next';

interface ImportarZipResultsProps {
	results: {
		documentsCorrectes: number;
		documentsError: number;
		documentsFirmaError: number;
		carpetesCreades: number;
		tamanyTotal: number;
		errorsDetall: string[];
		carpetesCreadesSet: string[];
	};
}

const HtmlContent: React.FC<{ error: string }> = ({ error }) => {
	const errorPlainText = error.replace(/<[^>]*>/g, '');

	return <span dangerouslySetInnerHTML={{ __html: error }}
		title={errorPlainText}
		style={{ whiteSpace: 'nowrap', overflow: 'hidden', textOverflow: 'ellipsis', display: 'block' }}
	/>
};

const ImportarZipResultsSimple: React.FC<ImportarZipResultsProps> = ({ results }) => {
	const { t } = useTranslation();

	const formatBytes = (bytes: number) => {
		if (bytes === 0) return '0 Bytes';
		const k = 1024;
		const sizes = ['Bytes', 'KB', 'MB', 'GB'];
		const i = Math.floor(Math.log(bytes) / Math.log(k));
		return parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + ' ' + sizes[i];
	};

	return (
		<Box sx={{ p: 2 }}>
			<Typography variant="h6" color="success.main" gutterBottom>
				{t('page.document.action.importZip.resultat.ok')}
			</Typography>

			<LinearProgress variant="determinate" value={100} color="success" sx={{ my: 2 }} />

			<Typography variant="subtitle1" gutterBottom>
				{t('page.document.action.importZip.resultat.title')}
			</Typography>

			<Box
				component="ul"
				sx={{
					borderRadius: 1,
					pl: 4,
					listStyleType: 'disc'
				}}
			>
				<Box component="li" key={0}>
					<Typography variant="body2">
						{t('page.document.action.importZip.resultat.documents.ok')}{results.documentsCorrectes}
					</Typography>
				</Box>

				{results.documentsError > 0 && (
					<Box component="li" key={1}>
						<Typography variant="body2" color="error">
							{t('page.document.action.importZip.resultat.documents.ko')}{results.documentsError}
						</Typography>
					</Box>
				)}

				{results.documentsFirmaError > 0 && (
					<Box component="li" key={2}>
						<Typography variant="body2" color="warning.main">
							{t('page.document.action.importZip.resultat.documents.firma')}{results.documentsFirmaError}
						</Typography>
					</Box>
				)}

				<Box component="li" key={3}>
					<Typography variant="body2">
						{t('page.document.action.importZip.resultat.carpetes.ok')}{results.carpetesCreades}
					</Typography>
				</Box>

				{results.tamanyTotal > 0 && (
					<Box component="li" key={4}>
						<Typography variant="body2">
							{t('page.document.action.importZip.resultat.tamany')}{formatBytes(results.tamanyTotal)}
						</Typography>
					</Box>
				)}
			</Box>

			{/* Carpetas creadas (lista detallada) */}
			{results.carpetesCreadesSet && results.carpetesCreadesSet.length > 0 && (
				<>
					<Typography variant="subtitle2" gutterBottom sx={{ mt: 2 }}>
						{t('page.document.action.importZip.resultat.carpetes.ok')} ({results.carpetesCreades}):
					</Typography>
					<Box
						component="ul"
						sx={{
							bgcolor: 'gainsboro',
							borderRadius: 1,
							p: 2,
							pl: 4,
							listStyleType: 'disc'
						}}
					>
						{results.carpetesCreadesSet.map((carpeta, index) => (
							<Box component="li" key={index}>
								<Typography variant="body2">
									{carpeta}
								</Typography>
							</Box>
						))}
					</Box>
				</>
			)}

			{/* Errores detallados */}
			{results.errorsDetall && results.errorsDetall.length > 0 && (
				<>
					<Typography variant="subtitle2" gutterBottom sx={{ mt: 2 }}>
						{t('page.document.action.importZip.resultat.errors')}
					</Typography>
					<Box
						component="ul"
						sx={{
							bgcolor: 'mistyrose',
							color: 'error.dark',
							borderRadius: 1,
							p: 2,
							pl: 4,
							listStyleType: 'disc'
						}}
					>
						{results.errorsDetall.map((error, index) => (
							<Box component="li" key={index}>
								<Typography variant="body2">
									<HtmlContent error={error} />
								</Typography>
							</Box>
						))}
					</Box>
				</>
			)}
		</Box>
	);
};

export default ImportarZipResultsSimple;