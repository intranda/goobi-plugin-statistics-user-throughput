title: Visualisation of the throughput per user
identifier: intranda_statistics_user_througput
published: true
description: Statistics plugin for visualising user throughput
---
## Introduction
This documentation describes the installation and usage of the User Throughput Plugin.

## Installation
To install the plugin, the following files need to be installed:

```bash
/opt/digiverso/goobi/plugins/GUI/plugin_intranda_statistics-user-throughput-GUI.jar
/opt/digiverso/goobi/plugins/statistics/plugin_intranda_statistics-user-throughput.jar
/opt/digiverso/goobi/plugins/statistics/user_throughput_template.xlsx
/opt/digiverso/goobi/plugins/statistics/user_throughput_template_process.xlsx
```
## Plugin Configuration

This plugin does not require any additional configuration.

## Using the Plugin

To limit the evaluation period, you can use the `Start Date` and `End Date` fields to specify the start and end dates. A date in the format `YYYY-MM-DD` can be entered. Both fields are optional. If the start date is not specified, the date when the first step was completed will be used. If the end date is not specified, the current time will be used.

In the `Unit` field, you can specify the time intervals in which the evaluation should be summarized. You can choose from `Years`, `Months`, `Weeks`, or `Days`.

In the `Display` field, you can specify which figures should be displayed. You can choose from `Pages` or `Processes`.

After clicking the `Calculate Statistics` button, the user throughput will be displayed in detailed tables. Below each table, there is also a link to download the table as an Excel file.