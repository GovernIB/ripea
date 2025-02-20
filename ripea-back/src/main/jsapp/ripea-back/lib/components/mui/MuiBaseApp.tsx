import React from 'react';
import { useBaseAppContext } from '../BaseAppContext';
import { useAnswerRequiredDialogButtons } from '../AppButtons';
import { BaseApp, BaseAppProps } from '../BaseApp';
import { useOptionalResourceApiContext } from '../ResourceApiContext';
import { useMessageDialog } from './Dialog';
import { useTemporalMessage } from './TemporalMessage';
import AppBar from './AppBar';
import Menu, { MenuEntry } from './Menu';
import OfflineMessage from './OfflineMessage';
import { useToolbarMenuIcon } from './ToolbarMenuIcon';
import { FormFieldCheckbox } from './form/FormFieldCheckbox';
import { FormFieldNumber } from './form/FormFieldNumber';
import { FormFieldText } from './form/FormFieldText';

export type MuiBaseAppProps = Omit<BaseAppProps, 'contentComponentSlots'> & {
    title?: string | React.ReactElement;
    footer?: React.ReactElement;
    version?: string;
    logo?: string;
    logoStyle?: any;
    menuTitle?: string;
    menuEntries?: MenuEntry[];
    menuOnTitleClose?: () => void;
    menuShrinkDisabled?: boolean;
    menuWidth?: number,
    additionalHeaderComponents?: React.ReactElement | React.ReactElement[];
    additionalAuthComponents?: React.ReactElement | React.ReactElement[];
    appbarStyle?: any;
    appbarBackgroundColor?: string;
    appbarBackgroundImg?: string;
};

const baseFormFieldComponents = [{
    type: 'checkbox',
    component: FormFieldCheckbox,
}, {
    type: 'number',
    component: FormFieldNumber,
}, {
    type: 'text',
    component: FormFieldText,
}, {
    type: 'textarea',
    component: FormFieldText,
}];

const MuiComponentsConfigurer: React.FC = () => {
    const [messageDialogShow, messageDialogComponent] = useMessageDialog();
    const [temporalMessageShow, temporalMessageComponent] = useTemporalMessage();
    const resourceApiContext = useOptionalResourceApiContext();
    const {
        setMessageDialogShow,
        setTemporalMessageShow,
    } = useBaseAppContext();
    const getAnswerRequiredButtons = useAnswerRequiredDialogButtons();
    const openAnswerRequiredDialog = (
        title: string | undefined,
        question: string,
        trueFalseAnswerRequired: boolean,
        availableAnswers?: string[]) => {
        return messageDialogShow(
            title ?? 'Atenció',
            question,
            getAnswerRequiredButtons(trueFalseAnswerRequired, availableAnswers));
    }
    React.useEffect(() => {
        setMessageDialogShow(messageDialogShow);
        setTemporalMessageShow(temporalMessageShow);
        if (resourceApiContext != null) {
            resourceApiContext.setOpenAnswerRequiredDialog(openAnswerRequiredDialog);
        }
    }, [])
    return <>
        {messageDialogComponent}
        {temporalMessageComponent}
    </>;
}

const useMenu = (
    menuTitle: string | undefined,
    menuEntries: MenuEntry[] | undefined,
    menuOnTitleClose: (() => void) | undefined,
    menuWidth: number | undefined) => {
    const {
        shrink,
        iconClicked,
        buttonComponent: menuButton } = useToolbarMenuIcon();
    const menuComponent = menuEntries != null ? <Menu
        title={menuTitle}
        entries={menuEntries}
        onTitleClose={menuOnTitleClose}
        drawerWidth={menuWidth}
        shrink={shrink}
        iconClicked={iconClicked} /> : undefined;
    return {
        menuButton,
        menuComponent
    }
}

export const MuiBaseApp: React.FC<MuiBaseAppProps> = (props) => {
    const {
        title,
        footer,
        version,
        logo,
        logoStyle,
        menuTitle,
        menuEntries,
        menuOnTitleClose,
        menuShrinkDisabled,
        menuWidth,
        appbarStyle,
        appbarBackgroundColor,
        appbarBackgroundImg,
        formFieldComponents,
        additionalHeaderComponents,
        additionalAuthComponents,
        children,
        ...otherProps
    } = props;
    const mergedFormFieldComponents = [...baseFormFieldComponents, ...(formFieldComponents ?? [])];
    const {
        menuButton,
        menuComponent
    } = useMenu(
        menuTitle,
        menuEntries,
        menuOnTitleClose,
        menuWidth);
    const appbarComponent = <AppBar
        title={title}
        version={version}
        logo={logo}
        logoStyle={logoStyle}
        menuButton={!menuShrinkDisabled && menuEntries != null ? menuButton : undefined}
        additionalToolbarComponents={additionalHeaderComponents}
        additionalAuthComponents={additionalAuthComponents}
        style={appbarStyle}
        backgroundColor={appbarBackgroundColor}
        backgroundImg={appbarBackgroundImg} />;
    const offlineComponent = <OfflineMessage />;
    return <BaseApp
        formFieldComponents={mergedFormFieldComponents}
        {...otherProps}
        contentComponentSlots={{
            appbar: appbarComponent,
            footer,
            menu: menuComponent,
            offline: offlineComponent,
        }}>
        <>
            <MuiComponentsConfigurer />
            {children}
        </>
    </BaseApp>;
}

export default MuiBaseApp;