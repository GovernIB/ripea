import {useResourceApiService} from "reactlib";
import {useEffect, useState} from "react";
import {Badge, Icon, IconButton, Typography} from "@mui/material";
import Dialog from "../../lib/components/mui/Dialog.tsx";
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
        find: findAll,
    } = useResourceApiService('expedientSeguidorResource');

    const [numFollowes, setNumFollowes] = useState<number>(entity?.numSeguidors);
    const [followes, setFollowes] = useState<any[]>([]);
	const [open, setOpen] = useState(false);

    useEffect(() => {
        if (appApiIsReady && !followes?.length){
            findAll({
                filter: `expedient.id:${entity?.id}`,
                sorts: ['seguidor.nom', 'desc']
            })
                .then((app) => {
                    setFollowes(app.rows);
                    setNumFollowes?.(app.rows.length)
                })
        }
    }, [appApiIsReady]);
	
    const handleOpen = () => {
		setOpen(true);
    }

	const handleClose = () => {
        setOpen(false);
    };
	
    return <>
		<IconButton aria-label="forum" color={"inherit"} onClick={handleOpen}>
	        <Badge badgeContent={numFollowes} color="primary">
	            <Icon>people</Icon>
	        </Badge>
	    </IconButton>	
	
		<Dialog
		    open={open}
			closeCallback={handleClose}
			title={t('page.expedient.modal.seguidors') +': '+ entity?.nom}
			key={entity?.id}
		    componentProps={{ fullWidth: true, maxWidth: 'sm' }}
		    buttons={[
		        {
		            value: 'close',
		            text: t('common.close'),
		            icon: 'close'
		        },
		    ]}
		    buttonCallback={(value :any) :void=>{
		        if (value=='close') {
		            handleClose();
		        }
		    }}>
            {followes?.map((a:any)=><Typography key={a?.seguidor?.id} sx={followerStyle}>{a?.seguidor?.description}</Typography>)}
        </Dialog>
    </>
}