import {useEffect, useState} from "react";
import {Badge, Icon, IconButton, Typography} from "@mui/material";
import {useResourceApiService, MuiDialog} from "reactlib";
import {useTranslation} from 'react-i18next';

const followerStyle = {
	borderRadius: 2,
	px: 2,
	py: 1,
	bgcolor: '#e0e0e0',
	color: 'rgba(0, 0, 0, 0.38)'}

export const FollowersDialog = (props:any) => {
	const { entity } = props;
    const { t } = useTranslation();

    const {
        isReady: appApiIsReady,
        getOne: apiGetOne
    } = useResourceApiService('expedientResource');

    const [followes, setFollowes] = useState<any[]>([]);
	const [open, setOpen] = useState(false);

    useEffect(() => {
        if (open && appApiIsReady){
            apiGetOne(entity?.id, {perspectives: ['FOLLOWERS']})
                .then((app) => {
                    setFollowes(app?.seguidors);
                })
        }
    }, [open]);
	
    const handleOpen = (event:any) => {
        event.stopPropagation()
		setOpen(true);
    }

    const handleClose = (reason?: string) => {
        if(reason !== 'backdropClick') {
            setOpen(false);
        }
    };
	
    return <>
		<IconButton title={t('page.expedient.modal.seguidors.label')} aria-label="forum" color={"inherit"} onClick={handleOpen}>
	        <Badge badgeContent={entity?.numSeguidors} color="primary" showZero>
	            <Icon>people</Icon>
	        </Badge>
	    </IconButton>	
	
		<MuiDialog
		    open={open}
			closeCallback={handleClose}
			title={t('page.expedient.modal.seguidors.title') +': '+ entity?.nom}
			key={entity?.id}
		    componentProps={{ fullWidth: true, maxWidth: 'sm' }}
		    buttons={[
		        {
		            value: 'close',
		            text: t('common.close'),
		            componentProps: { variant: 'outlined' }
		        },
		    ]}
		    buttonCallback={(value :any) :void=>{
		        if (value=='close') {
		            handleClose();
		        }
		    }}>
            {followes?.map((a:any)=><Typography key={a?.id} sx={followerStyle}>{a?.description}</Typography>)}
        </MuiDialog>
    </>
}