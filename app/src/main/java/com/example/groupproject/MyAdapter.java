package com.example.groupproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseExpandableListAdapter {

    Context context;
    List<String> parentList;
    Map<String, List<String>> childMap;

    public MyAdapter(Context context, List<String> parentList, Map<String, List<String>> childMap) {
        this.context = context;
        this.parentList = parentList;
        this.childMap = childMap;
    }

    @Override
    public int getGroupCount() {
        return parentList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> children = childMap.get(parentList.get(groupPosition));
        return (children != null) ? children.size() : 0;
    }


    @Override
    public Object getGroup(int groupPosition) {
        return parentList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<String> children = childMap.get(parentList.get(groupPosition));
        return (children != null && childPosition < children.size()) ? children.get(childPosition) : null;
    }


    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    // Use parent.xml for group view (name)
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.parent, parent, false);
        }

        TextView groupText = convertView.findViewById(R.id.parentList); // Ensure the ID matches your parent.xml
        groupText.setText(groupTitle);

        return convertView;
    }

    // Use child_list.xml for child view (details)
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        String childDetail = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_list, parent, false);
        }

        TextView childText = convertView.findViewById(R.id.childList); // Ensure the ID matches your child_list.xml
        childText.setText(childDetail);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
