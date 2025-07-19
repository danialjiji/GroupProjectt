package com.example.groupproject;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.LinearLayout;
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

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String groupTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.parent, parent, false);
        }

        TextView groupText = convertView.findViewById(R.id.parentList);
        groupText.setText(groupTitle);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild,
                             View convertView, ViewGroup parent) {

        String childItem = (String) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.child_list, parent, false);
        }

        TextView childText = convertView.findViewById(R.id.childText);
        LinearLayout buttonLayout = convertView.findViewById(R.id.buttonLayout);
        View btnUpdate = convertView.findViewById(R.id.btnUpdate);
        View btnDelete = convertView.findViewById(R.id.btnDelete);
        View btnBirthday = convertView.findViewById(R.id.btnBirthday);

        // Reset all views to GONE first
        childText.setVisibility(View.GONE);
        buttonLayout.setVisibility(View.GONE);
        btnUpdate.setVisibility(View.GONE);
        btnDelete.setVisibility(View.GONE);
        btnBirthday.setVisibility(View.GONE);

        if (childItem.equals("Actions")) {
            buttonLayout.setVisibility(View.VISIBLE);
            btnUpdate.setVisibility(View.VISIBLE);
            btnDelete.setVisibility(View.VISIBLE);
            btnBirthday.setVisibility(View.VISIBLE);

            btnUpdate.setOnClickListener(v -> {
                if (context instanceof FriendList) {
                    ((FriendList) context).handleUpdateClick(groupPosition);
                }
            });

            btnDelete.setOnClickListener(v -> {
                if (context instanceof FriendList) {
                    ((FriendList) context).handleDeleteClick(groupPosition);
                }
            });

            btnBirthday.setOnClickListener(v -> {
                if (context instanceof FriendList) {
                    ((FriendList) context).handleBirthdayClick(groupPosition);
                }
            });

        } else if (!childItem.matches("\\d+")) {
            childText.setText(childItem);
            childText.setVisibility(View.VISIBLE);
        }


        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }
}
