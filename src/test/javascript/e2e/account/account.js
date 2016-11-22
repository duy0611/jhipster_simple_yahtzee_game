'use strict';

describe('account', function () {

    var username = element(by.id('username'));
    var accountMenu = element(by.id('account-menu'));
    var login = element(by.id('login'));
    var logout = element(by.id('logout'));

    beforeAll(function () {
        browser.get('/');
    });

    it('should login successfully with admin account', function () {
        expect(element.all(by.css('h1')).first().getText()).toMatch(/Sign in/);

        username.clear().sendKeys('admin');
        element(by.css('button[type=submit]')).click();

        expect(element(by.css('.alert-success')).getText()).toMatch(/You are logged in as user "admin"/);
    });
    
    it('should login successfully with user account', function () {
        expect(element.all(by.css('h1')).first().getText()).toMatch(/Sign in/);

        username.clear().sendKeys('user');
        element(by.css('button[type=submit]')).click();

        expect(element(by.css('.alert-success')).getText()).toMatch(/You are logged in as user "user"/);
    });

    afterAll(function () {
        accountMenu.click();
        logout.click();
    });
});
