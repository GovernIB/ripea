const componentsEn = {
    app: {
        menu: {
            home: 'Home',
        },
        auth: {
            logout: 'Log out',
        },
        error: {
            '404': 'Not found',
            '503': 'No connection',
        },
        offline: {
            message: 'Server connection lost',
        }
    },
    datacommon: {
        details: {
            title: 'Details',
        },
        create: {
            title: 'Create',
        },
        update: {
            title: 'Update',
        },
        delete: {
            title: 'Delete',
            single: {
                title: 'Delete',
                confirm: 'Are you sure you want to delete this item (this action can\'t be undone)?',
                success: 'Deleted items',
                error: 'Error deleting items',
            },
        },
        export: {
            title: 'Export',
        },
        refresh: {
            title: 'Refresh',
        },
        toolbar: {
            error: 'An error has occurred',
        },
        noRows: 'No data',
    },
    form: {
        goBack: {
            title: 'Go back',
        },
        revert: {
            title: 'Undo changes',
            confirm: 'Are you sure you want to revert the changes in the form?',
        },
        create: {
            title: 'Create',
            success: 'Element created',
            error: 'Error creating element',
        },
        update: {
            title: 'Update',
            success: 'Element updated',
            error: 'Error updating element',
            wrong_resource_type: 'Couldn\'t save forms with resource type "{{resourceType}}"',
        },
        delete: {
            title: 'Delete',
            confirm: 'Are you sure you want to delete this item (this action can\'t be undone)?',
            success: 'Element deleted',
            error: 'Error deleting element',
        },
        field: {
            reference: {
                open: 'Open',
                close: 'Close',
                clear: 'Clear',
                loading: 'Loading...',
                noOptions: 'No options',
                page: 'Showing {{size}} out of {{totalElements}} elements',
                advanced: {
                    title: 'Select value'
                }
            },
            checkboxSelect: {
                true: 'Yes',
                false: 'No',
            },
        },
        dialog: {
            create: 'Create',
            update: 'Update',
        },
        validate: {
            error: 'Validation error',
        },
    },
    grid: {
        selection: {
            one: '1 selected row ',
            multiple: '{{count}} selected rows',
        },
    },
    buttons: {
        answerRequired: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        confirm: {
            accept: 'Accept',
            cancel: 'Cancel',
        },
        form: {
            save: 'Save',
            cancel: 'Cancel',
        },
        action: {
            exec: 'Execute',
            cancel: 'Cancel',
        },
        report: {
            generate: 'Generate',
            cancel: 'Cancel',
        },
        misc: {
            close: 'Close',
            retry: 'Retry',
        },
    },
};

export default componentsEn;