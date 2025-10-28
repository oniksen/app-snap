# app-snap
Мобильное приложение отображающее информацию об установленных мобильных приложениях.

## Главная страница:
Показывает список установленных приложений на устройстве. При каждом сканировании списка, расчитывается хэш-сумма пакетов приложения (по sha-256) и сравнивается с уже имеющейся. Если между старой и новой суммой будет разница, то будет отображена иконка-предупреждение, по нажатию на которую можно будет сохранить изменения, т.е. показать, что вы в курсе изменений.

### Список установленных приложений
| <div align="center"><strong>Портретный режим</strong></div> | <div align="center"><strong>Альбомный режим</strong></div> |
|:---:|:---:|
| <div align="center"><a href="apps-list-portrait"><img src="https://github.com/user-attachments/assets/b25c9c3c-36d4-4f2c-921d-960204ddd5fd" width="100%" style="max-width: 200px; height: auto;" alt="Портретный режим"></a></div> | <div align="center"><a href="apps-list-landscape"><img src="https://github.com/user-attachments/assets/ca6fa4cc-5bc7-4203-8522-e49b97b96850" width="100%" style="max-width: 400px; height: auto;" alt="Альбомный режим"></a></div> |

### Особенности

#### Поддержка pull-to-refresh
<a href="refresh-list">
  <img 
    src="https://github.com/user-attachments/assets/9df23150-39b2-4c86-92f9-145bb6b3cb35" 
    align="left" 
    width="150" 
    style="height: auto; max-width: 100%; object-fit: contain;">
</a>
<br clear="left"/>

## Страница детального описания выбранного приложения:
Здесь отображается некоторая информация о приложении, а так же кнопка, с возможностью запустить приложение. При несовпадении хэшей, UI изменится. По нажатию на кнопку "Сохранить изменения" можно будет отметить, что вы в курсе этих изменений.

| <div align="center"><strong>Портретный режим</strong></div> | <div align="center"><strong>Альбомный режим</strong></div> |
|:---:|:---:|
| <div align="center"><a href="modified-app-page-portrait"><img src="https://github.com/user-attachments/assets/a5c52eb4-2bd8-4234-840a-540804f04fb7" width="100%" style="max-width: 200px; height: auto;" alt="Портретный режим"></a></div> | <div align="center"><a href="modified-app-page-landscape"><img src="https://github.com/user-attachments/assets/aea6af3e-e05c-4c08-85f0-8891d54df3fd" width="100%" style="max-width: 400px; height: auto;" alt="Альбомный режим"></a></div> |
