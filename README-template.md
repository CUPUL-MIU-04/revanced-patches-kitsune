# 🦊 ReVanced Patches Kitsune

Custom patches for ReVanced with extended functionality and Kitsune-specific enhancements.

🔗 **Documentation**: [How to apply patches](https://github.com/CUPUL-MIU-04/revanced-patches-kitsune/docs)  
📦 **Latest Version**: ![](https://img.shields.io/github/v/release/CUPUL-MIU-04/revanced-patches-kitsune?color=blue&include_prereleases)

## ✨ Key Features
- Extended compatibility beyond official ReVanced
- Kitsune-exclusive patches
- Regular updates with new features
- Optimized performance patches

## 📋 Available Patches

{{ table }}

## 🛠️ Configuration Guide

### Basic patches.json structure:

```json
{
  "name": "Kitsune Premium Unlock",
  "description": "Unlocks premium features in supported apps",
  "version": "1.0.0",
  "compatiblePackages": {
    "com.google.android.youtube": "COMPATIBLE_PACKAGE_YOUTUBE",
    "com.google.android.apps.youtube.music": "COMPATIBLE_PACKAGE_MUSIC",
    "com.reddit.frontpage": 
  "COMPATIBLE_PACKAGE_REDDIT"
  },
  "options": [
    {
      "key": "enable_premium",
      "title": "Enable Premium Features",
      "description": "Unlock all premium content",
      "required": true,
      "choices": ["true", "false"],
      "default": "true"
    }
  ]
}
