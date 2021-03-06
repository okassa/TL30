![AEM Adobe](chapters/images/logo/Lab-Header.png)  
## Technical Lab 30 : Building a Progressive Web Application with AEM

**Lab overview** 

 
Marketing and Technical teams have faced many challenges since a decade to build compelling mobile experiences: technical adoption 
of platforms specific languages (Objective-C for iOS, Java for Android), poor user experience for mobile applications built onto 
cross platforms engine (Phonegap, Xamarin...). The issue remained the same:  **How can we build web apps that look and feel like 
native mobile apps for iOS and Android?**

This question has been answered by Google Engineering Teams: Build Progressive Web Applications.PWA will help marketers deliver fast, 
engaging and reliable experiences for customers on mobile. Having a PWA is not just a mean to achieve a great digital 
transformation, it's one of the key factor for customer retention and brand intimacy.
  
 ![AEM Adobe PWA](chapters/images/others/pwa-icon.jpg)
 
**Scope**
 
In this training you will learn how to get started with Progressive Web Applications wth AEM.It could be a 
good starting point for extending an existing website or building a new one as a progressive web application.
This technical lab aims to create a web application (HTML, CSS, JavaScript) within AEM with PWA capabilities
 
#### What you'll learn

- Access to native device features (camera)
- Use service workers (JavaScript) for offline mode
- Leveraging Caching API efficiently
- Send web push notifications to users
- Test your PWA on a Virtual Device (Android)

#### What you'll need

- Chrome 70 or above
- An AEM 6.5 publish instance 
- An Android emulator for mobile testing
- Basic knowledge of HTML, CSS, JavaScript, and Chrome DevTools
- Good understanding of AEM technical platform 

#### Materials

- [Package](chapters/aem-pwa-blog.ui.reactor-1.4.0.zip) 
- [Lessons](chapters/lab30-final.pdf)

#### Push Notifications

- You will need to create a firebase project and create a configuration for FirebaseNotificationServiceImpl
at /apps/aem-pwa-blog/config/com.adobe.summit.emea.core.services.impl.FirebaseNotificationServiceImpl.xml
