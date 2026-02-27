import React from 'react';
import { SimpleTreeView } from '@mui/x-tree-view/SimpleTreeView';
import { TreeItem } from '@mui/x-tree-view/TreeItem';
import { useResourceApiService } from 'reactlib';
import * as builder from "../../../util/springFilterUtils.ts";

const PropietatsGroupTreeItems: React.FC<{
    configGroups?: any[];
    parentId?: any;
}> = (props) => {
    const { configGroups, parentId } = props;
    const configGroupsFilterByParentId = (parentId?: any) => {
        return configGroups?.filter((g) => (parentId ?? null) === (g.parent?.id ?? null));
    };
    const filteredGroups = configGroupsFilterByParentId(parentId);
    return filteredGroups?.map((g) => (
        <TreeItem key={g.key} itemId={g.id} label={g.description}>
            {configGroupsFilterByParentId(g.id)?.length ? (
                <PropietatsGroupTreeItems configGroups={configGroups} parentId={g.id} />
            ) : null}
        </TreeItem>
    ));
};

export const PropietatsGroups: React.FC<{
    quickFilter?: string;
    onChange: (group: any) => void;
}> = (props) => {
    const { quickFilter, onChange } = props;
    const { isReady: apiIsReady, find: apiFind } = useResourceApiService('configGroupResource');
    const [configGroups, setConfigGroups] = React.useState<any[]>();
    const [selectedGroupId, setSelectedGroupId] = React.useState<string>();
    const [selectedItems, setSelectedItems] = React.useState<string>('');
    React.useEffect(() => {
        if (apiIsReady) {
            const args = {
                filter: quickFilter?.length
                    ? builder.or(
                        builder.exists(
                            builder.or(
                                builder.like('configs.key', quickFilter),
                                builder.like('configs.description', quickFilter),
                                builder.like('configs.value', quickFilter),
                            ),
                        ),
                        builder.exists(
                            builder.or(
                                builder.like('children.configs.key', quickFilter),
                                builder.like('children.configs.description', quickFilter),
                                builder.like('children.configs.value', quickFilter),
                            ),
                        ),
                    )
                    :undefined,
                sorts: ['position,asc'],
                unpaged: true,
            };
            apiFind(args).then((response) => {
                const configGroups = response.rows;
                setConfigGroups(configGroups);
                if (configGroups.length) {
                    const isSelectedGroupIdInConfigGroups = configGroups.find(
                        (g) => g.id === selectedGroupId
                    );
                    if (!isSelectedGroupIdInConfigGroups) {
                        setSelectedGroupId(response.rows[0].id);
                        setSelectedItems('' + response.rows[0].id);
                    }
                }
            });
        }
    }, [apiIsReady, quickFilter]);

    React.useEffect(() => {
        setSelectedItems('' + selectedGroupId);
        onChange?.(configGroups?.find((g) => g.id == selectedGroupId));
    }, [selectedGroupId]);
    return (
        <SimpleTreeView
            selectedItems={selectedItems}
            onSelectedItemsChange={(_event, id) => {
                const childrens:number = configGroups
                    ?.find(item=>item.id==id)
                    ?.childrens || 0
                if (childrens == 0)
                    setSelectedGroupId(id || undefined)
            }}
            sx={{
                '& .MuiTreeItem-content': {
                    // minHeight: 48,
                    paddingY: 1,
                },
            }}>
            <PropietatsGroupTreeItems configGroups={configGroups} />
        </SimpleTreeView>
    );
};
