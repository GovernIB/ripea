import React from 'react';
import { Typography, Box, Backdrop, LinearProgress, Button } from '@mui/material';
import { useBaseAppContext, useConfirmDialogButtons } from 'reactlib'; // ← Importar el contexto
import { useTranslation } from 'react-i18next';

interface ImportarZipBackdropProps {
	open: boolean;
	progress: number;
	progressMessage: string;
	onCancel: () => void;
	onClose: () => void;
}

const ImportarZipBackdrop: React.FC<ImportarZipBackdropProps> = ({
	open,
	progress,
	progressMessage,
	onCancel,
	onClose
}) => {
	const { t } = useTranslation();
	const { messageDialogShow } = useBaseAppContext();
	const confirmDialogButtons = useConfirmDialogButtons();

	const handleCancelClick = () => {
		messageDialogShow(
			t('page.document.action.importZip.cancel.check'),
			t('page.document.action.importZip.cancel.description'),
			confirmDialogButtons,
			{
				maxWidth: 'sm',
				fullWidth: true,
				sx: { zIndex: 1500 }
			}
		).then((value: any) => {
			if (value) {
				onCancel();
			}
		});
	};

	const handleCloseClick = () => {
		messageDialogShow(
			t('page.document.action.importZip.close.check'),
			t('page.document.action.importZip.close.description'),
			confirmDialogButtons,
			{
				maxWidth: 'sm',
				fullWidth: true,
				sx: { zIndex: 1500 }
			}
		).then((value: any) => {
			if (value) {
				onClose();
			}
		});
	};

	return (
		<Backdrop
			open={open}
			sx={{
				zIndex: 1400,
				backgroundColor: 'rgba(0, 0, 0, 0.8)'
			}}
		>
			<Box
				sx={{
					p: 4,
					bgcolor: 'background.paper',
					width: '900px',
					maxWidth: '90%',
					textAlign: 'center',
					borderRadius: 1,
					boxShadow: 24
				}}
			>

				<LinearProgress
					variant="determinate"
					value={progress}
					sx={{
						height: 6,
						borderRadius: 2,
						mt: 2,
						mb: 2
					}}
				/>

				<Typography
					variant="body2"
					color="text.secondary"
					gutterBottom
				>
					{progress || 0}%
				</Typography>

				<Typography
					variant="body1"
					color="text.primary"
					mb={3}
				>
					<span
						dangerouslySetInnerHTML={{ __html: progressMessage || t('common.processing') }}
						style={{
							whiteSpace: 'nowrap',
							overflow: 'hidden',
							textOverflow: 'ellipsis',
							display: 'block'
						}}
					/>
				</Typography>

				<Box sx={{ mt: 3, display: 'flex', justifyContent: 'center', gap: 2 }}>
					<Button
						variant="outlined"
						color="error"
						onClick={handleCancelClick}
					>
						{t('common.cancel')}
					</Button>
					<Button
						variant="outlined"
						color="primary"
						onClick={handleCloseClick}
					>
						{t('common.close')}
					</Button>
				</Box>
			</Box>
		</Backdrop>
	);
};

export default ImportarZipBackdrop;