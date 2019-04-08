![AEM Adobe](../chapters/images/Lab-Header.png)  

## 2. Update your manifest.json file

### Change application properties

- Go back to your CRXDE.

- Manifest content for TL30 PWA

```json
{
  "gcm_sender_id": "294077202000",
  "orientation": "portrait",
  "theme_color": "#003c7f",
  "display": "fullscreen",
  "start_url": "/content/aem-pwa-blog/en.html",
  "description": "PWA-TL30 is a progressive web application powerded by Adobe Experience Manager",
  "dir": "ltr",
  "icons": [
    {
      "sizes": "64x64",
      "src": "/etc/clientlibs/aem-pwa-blog/icons/summit-icon.png"
    },
    {
      "sizes": "48x48",
      "src": "/etc/clientlibs/aem-pwa-blog/icons/summit-icon-48x48.png"
    },
    {
      "sizes": "96x96",
      "src": "/etc/clientlibs/aem-pwa-blog/icons/summit-icon-96x96.png"
    },
    {
      "sizes": "144x144",
      "src": "/etc/clientlibs/aem-pwa-blog/icons/summit-icon-144x144.png"
    },
    {
      "sizes": "192x192",
      "src": "/etc/clientlibs/aem-pwa-blog/icons/summit-icon-192x192.png"
    },
    {
      "sizes": "256x256",
      "src": "/etc/clientlibs/aem-pwa-blog/icons/summit-icon-256x256.png"
    }
  ],
  "serviceworker": {
    "update_via_cache": "none",
    "src": "/content/aem-pwa-blog/sw.js",
    "scope": "/content/aem-pwa-blog/home.html"
  },
  "background_color": "#003c7f",
  "scope": "/content/aem-pwa-blog/",
  "name": "PWA-TL30",
  "gcm_user_visible_only": true,
  "short_name": "PWA-TL30",
  "lang": "en"
}

```
- Open the manifest.json at this location TL30-PWA > /content/adobe-summit-emea-2019/tl30-pwa/manifest.json

Go to the next chapter : [Access the emulator camera](chapter-3.md)