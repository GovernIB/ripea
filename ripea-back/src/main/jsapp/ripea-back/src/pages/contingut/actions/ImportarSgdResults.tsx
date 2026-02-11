import React from 'react';
import { Box, Typography, LinearProgress } from '@mui/material';
import { useTranslation } from 'react-i18next';

interface ImportarSgdResultsProps {
	results: {
		documentsImportats: number;
		interessatsImportats: number;
		carpetesCreades: number;
		errorsDetall: string[];
	};
}

const HtmlContent: React.FC<{ error: string }> = ({ error }) => {
	const errorPlainText = error.replace(/<[^>]*>/g, '');
	return (
		<span
			dangerouslySetInnerHTML={{ __html: error }}
			title={errorPlainText}
			style={{
				whiteSpace: 'nowrap',
				overflow: 'hidden',
				textOverflow: 'ellipsis',
				display: 'block',
			}}
		/>
	);
};

const ImportarSgdResults: React.FC<ImportarSgdResultsProps> = ({ results }) => {
	const { t } = useTranslation();

	return (
		<Box sx={{ p: 2 }}>
			<Typography variant="h6" color="success.main" gutterBottom>
				{t('page.document.action.importSgd.resultat.ok')}
			</Typography>

			<LinearProgress
				variant="determinate"
				value={100}
				color="success"
				sx={{ my: 2 }}
			/>

			<Typography variant="subtitle1" gutterBottom>
				{t('page.document.action.importSgd.resultat.title')}
			</Typography>

			<Box
				component="ul"
				sx={{
					borderRadius: 1,
					pl: 4,
					listStyleType: 'disc',
				}}
			>
				<Box component="li" key={0}>
					<Typography variant="body2">
						{t('page.document.action.importSgd.resultat.documents')} {results.documentsImportats}
					</Typography>
				</Box>

				<Box component="li" key={1}>
					<Typography variant="body2">
						{t('page.document.action.importSgd.resultat.interessats')} {results.interessatsImportats}
					</Typography>
				</Box>

				<Box component="li" key={2}>
					<Typography variant="body2">
						{t('page.document.action.importSgd.resultat.carpetes')} {results.carpetesCreades}
					</Typography>
				</Box>
			</Box>

			{/* Errores detallados */}
			{results.errorsDetall && results.errorsDetall.length > 0 && (
				<>
					<Typography variant="subtitle2" gutterBottom sx={{ mt: 2 }}>
						{t('page.document.action.importSgd.resultat.errors')}
					</Typography>
					<Box
						component="ul"
						sx={{
							bgcolor: 'mistyrose',
							color: 'error.dark',
							borderRadius: 1,
							p: 2,
							pl: 4,
							listStyleType: 'disc',
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

export default ImportarSgdResults;
