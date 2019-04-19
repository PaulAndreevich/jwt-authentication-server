<#import "macro/common.ftl" as common>

<@common.page>
<h2>List of users</h2>
<table>
    <thead>
    <tr>
        <th>ID</th>
        <th>Username</th>
        <th>Email</th>
        <th>Password</th>
        <th>Role</th>
        <th></th>
    </tr>
    </thead>
    <tbody>
    <#list users as user>
    <tr>
        <td>${user.id}</td>
        <td>${user.username}</td>
        <td>${user.email}</td>
        <td>secret_password</td>
        <td><#list user.roles as role>${role}<#sep>, </#list></td>
    </tr>
    </#list>
    </tbody>
</table>
</@common.page>