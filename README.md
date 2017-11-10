[ ![Download](https://api.bintray.com/packages/imany/widget_maven/widget/images/download.svg) ](https://bintray.com/imany/widget_maven/widget/_latestVersion)

# install
app build.gradle
```
implementation 'apk.imany.widget:widget:1.0.0'
```

<img src="https://github.com/Imany-apk/widget/blob/master/scanner.gif" height="400px" /> <img src="https://github.com/Imany-apk/widget/blob/master/scanner2.gif" height="400px" />

## simplest use of ScannerView

```
<apk.imany.widget.ScannerView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:layout_weight="1"
    />
```

ScannerView's Attributes

```
sc_background // setting background
sc_color_first, sc_color_second // set same color for solid
sc_padding // setting padding
sc_back_light // for draw outside of circle
sc_mini_top_left, sc_mini_top_right, sc_mini_bottom_left, sc_mini_bottom_right, sc_mini_all // mini scanners boolean
sc_shadow_radius // in dp
sc_shadow_color // shadow color
sc_speed // 1 <= speed <= 5
```



## simplest use of RadarView

```
<apk.imany.widget.RadarView
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    />
```

RadarView's Attributes

```
rd_background // setting background
rd_color_first, rd_color_second // set same color for solid
rd_padding // setting padding
rd_shadow_radius // in dp
rd_shadow_color // shadow color
rd_speed // 1 <= speed <= 5
```
