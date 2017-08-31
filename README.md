Student Performance Dashboard
=================

This building block adds a page that gives a real-time top-level view of student performance across all accessible classes. Accessibility is based on two things: (1) classes you are teaching, and (2) classes you have Course Administrator access to via the Institutional Hierarchy. If you don't fit either of those categories (i.e. you are a student) access is denied.

The building block exposes a Tool that can be added to the Tools module on the My Blackboard page, and a preview module that gives the instructor the totals without them having to click in. After the building block is installed one or both of these must be added to the My Blackboard page by an administrator. Otherwise, the only means to access the module is via the Sysadmin tab using the link under the Tools and Utilies section.

A rendering hook hides the link and module from anyone who wouldn't have access to it (i.e. students).

How to develop for this project
====

Clone this repository into a directory: 
`git clone https://github.com/OSCELOT/wvn-perfdash.git -o upstream <project name>`

Note that the clone command has assigned the `upstream` remote identifier to keep it separated from your real origin remote.

Next, add an origin remote for your own git repository: 
`git remote add origin <URL to your git repo>`

Add your code and push to your origin remote: 
`git push origin`

Deploying Your B2
===
To deploy your B2 for testing, run `gradlew deployB2`. For auto-deploy to work you will need to update the ext {} section of build.gradle, and install the Starting Block from Blackboard to your instance. This Starting Block is a grave security risk because it exposes hooks that allow anyone who knows about it to install arbitrary code on your instance. You have been warned.
