package com.happysong.android;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happysong.android.entity.City;
import com.happysong.android.entity.District;
import com.happysong.android.entity.Grade;
import com.happysong.android.entity.School;
import com.happysong.android.entity.TeamClass;
import com.happysong.android.net.NetConstant;
import com.happysong.android.util.DefaultParam;
import com.happysong.android.view.SimpleStringPopup;
import com.londonx.lutil.entity.LResponse;
import com.londonx.lutil.util.LRequestTool;
import com.londonx.lutil.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

/**
 * Created by london on 15/10/26.
 * 筛选
 */
public class FilterActivity extends AppCompatActivity implements
        LRequestTool.OnResponseListener,
        SimpleStringPopup.SimpleStringListener {
    private static final int API_CITIES = 1;
    private static final int API_DISTRICTS = 2;
    private static final int API_SCHOOLS = 3;
    private static final int API_GRADES = 4;
    private static final int API_TEAM_CLASSES = 5;

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.activity_filter_tvCity)
    TextView activityFilterTvCity;
    @Bind(R.id.activity_filter_tvArea)
    TextView activityFilterTvArea;
    @Bind(R.id.activity_filter_tvSchool)
    TextView activityFilterTvSchool;
    @Bind(R.id.activity_filter_tvGrade)
    TextView activityFilterTvGrade;
    @Bind(R.id.activity_filter_tvTeamClass)
    TextView activityFilterTvTeamClass;
    @Bind(R.id.activity_filter_lnGradeClass)
    LinearLayout activityFilterLnGradeClass;
    @Bind(R.id.activity_filter_lnGradeClassSelector)
    LinearLayout activityFilterLnGradeClassSelector;
    @Bind(R.id.activity_filter_divGradeClass)
    View activityFilterDivGradeClass;
    @Bind(R.id.activity_filter_root)
    LinearLayout activityFilterRoot;

    private List<City> cities;
    private List<District> districts;
    private List<School> schools;
    private List<Grade> grades;
    private List<TeamClass> teamClasses;

    private City selectedCity;
    private District selectedDistrict;
    private School selectedSchool;
    private Grade selectedGrade;
    private TeamClass selectedTeamClass;

    private SimpleStringPopup cityPopup;
    private SimpleStringPopup districtPopup;
    private SimpleStringPopup schoolPopup;
    private SimpleStringPopup gradePopup;
    private SimpleStringPopup teamClassPopup;

    LRequestTool requestTool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filter);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        if (ab != null) {
            ab.setDisplayHomeAsUpEnabled(true);
        }
        requestTool = new LRequestTool(this);
        boolean isRecommend = getIntent().getBooleanExtra("isRecommend", false);
        if (isRecommend) {
            activityFilterLnGradeClass.setVisibility(View.GONE);
            activityFilterLnGradeClassSelector.setVisibility(View.GONE);
            activityFilterDivGradeClass.setVisibility(View.INVISIBLE);
        }
        initSelectedData();
        requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_CITIES, new DefaultParam(), API_CITIES);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_filter, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        } else if (item.getItemId() == R.id.action_done) {
            Intent data = new Intent();
            data.putExtra("selectedCity", selectedCity);
            data.putExtra("selectedDistrict", selectedDistrict);
            data.putExtra("selectedSchool", selectedSchool);
            data.putExtra("selectedGrade", selectedGrade);
            data.putExtra("selectedTeamClass", selectedTeamClass);
            setResult(RESULT_OK, data);
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initSelectedData() {
        activityFilterTvCity.setText(R.string.all);
        activityFilterTvArea.setText(R.string.all);
        activityFilterTvSchool.setText(R.string.all);
        activityFilterTvGrade.setText(R.string.all);
        activityFilterTvTeamClass.setText(R.string.all);
    }

    @Override
    public void onResponse(LResponse response) {
        if (response.responseCode == 401) {
            Intent loginIntent = new Intent(this, LoginActivity.class);
            loginIntent.putExtra("isReLogin", true);
            startActivity(loginIntent);
            return;
        }
        if (response.responseCode == 0) {
            ToastUtil.show(R.string.toast_server_err_0);
            return;
        }
        if (response.responseCode != 200) {
            ToastUtil.serverErr(response);
            return;
        }
        if (!response.body.startsWith("[") && !response.body.startsWith("{")) {
            ToastUtil.show(R.string.toast_server_err_1);
            return;
        }
        switch (response.requestCode) {
            case API_CITIES:
                cities = new Gson().fromJson(response.body, new TypeToken<List<City>>() {
                }.getType());

                City city = new City();
                city.id = 0;
                city.name = getString(R.string.all);
                selectedCity = city;
                cities.add(0, city);

                List<String> cityStrings = new ArrayList<>();
                for (City c : cities) {
                    cityStrings.add(c.name);
                }
                cityPopup = new SimpleStringPopup(this, activityFilterRoot);
                cityPopup.setStrings(cityStrings);
                cityPopup.setSimpleStringListener(this);
                break;
            case API_DISTRICTS:
                districts = new Gson().fromJson(response.body, new TypeToken<List<District>>() {
                }.getType());

                District district = new District();
                district.id = 0;
                district.name = getString(R.string.all);
                selectedDistrict = district;
                districts.add(0, district);

                List<String> districtStrings = new ArrayList<>();
                for (District d : districts) {
                    districtStrings.add(d.name);
                }
                districtPopup = new SimpleStringPopup(this, activityFilterRoot);
                districtPopup.setStrings(districtStrings);
                districtPopup.setSimpleStringListener(this);
                break;
            case API_SCHOOLS:
                schools = new Gson().fromJson(response.body, new TypeToken<List<School>>() {
                }.getType());

                School school = new School();
                school.id = 0;
                school.name = getString(R.string.all);
                selectedSchool = school;
                schools.add(0, school);

                List<String> schoolStrings = new ArrayList<>();
                for (School s : schools) {
                    schoolStrings.add(s.name);
                }
                schoolPopup = new SimpleStringPopup(this, activityFilterRoot);
                schoolPopup.setStrings(schoolStrings);
                schoolPopup.setSimpleStringListener(this);
                break;
            case API_GRADES:
                grades = new Gson().fromJson(response.body, new TypeToken<List<Grade>>() {
                }.getType());

                Grade grade = new Grade();
                grade.id = 0;
                grade.name = getString(R.string.all);
                selectedGrade = grade;
                grades.add(0, grade);

                List<String> gradeStrings = new ArrayList<>();
                for (Grade g : grades) {
                    gradeStrings.add(g.name);
                }
                gradePopup = new SimpleStringPopup(this, activityFilterRoot);
                gradePopup.setStrings(gradeStrings);
                gradePopup.setSimpleStringListener(this);
                break;
            case API_TEAM_CLASSES:
                teamClasses = new Gson().fromJson(response.body, new TypeToken<List<TeamClass>>() {
                }.getType());

                TeamClass teamClass = new TeamClass();
                teamClass.id = 0;
                teamClass.name = getString(R.string.all);
                selectedTeamClass = teamClass;
                teamClasses.add(0, teamClass);

                List<String> teamClassStrings = new ArrayList<>();
                for (TeamClass t : teamClasses) {
                    teamClassStrings.add(t.name);
                }
                teamClassPopup = new SimpleStringPopup(this, activityFilterRoot);
                teamClassPopup.setStrings(teamClassStrings);
                teamClassPopup.setSimpleStringListener(this);
                break;
        }
    }

    @Override
    public void selectStringAt(SimpleStringPopup popup, int index) {
        if (popup.equals(cityPopup)) {
            if (!cities.get(index).equals(selectedCity)) {
                districts = null;
                districtPopup = null;
                selectedDistrict = null;
                activityFilterTvArea.setText(R.string.all);

                schools = null;
                schoolPopup = null;
                selectedSchool = null;
                activityFilterTvSchool.setText(R.string.all);

                grades = null;
                gradePopup = null;
                selectedGrade = null;
                activityFilterTvGrade.setText(R.string.all);

                teamClasses = null;
                teamClassPopup = null;
                selectedTeamClass = null;
                activityFilterTvTeamClass.setText(R.string.all);
            }
            selectedCity = cities.get(index);
            activityFilterTvCity.setText(selectedCity.name);
            if (selectedCity.id != 0) {
                DefaultParam param = new DefaultParam();
                param.put("city_id", selectedCity.id);
                requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_DISTRICTS, param, API_DISTRICTS);
            }
        } else if (popup.equals(districtPopup)) {
            if (!districts.get(index).equals(selectedDistrict)) {
                schools = null;
                schoolPopup = null;
                selectedSchool = null;
                activityFilterTvSchool.setText(R.string.all);

                grades = null;
                gradePopup = null;
                selectedGrade = null;
                activityFilterTvGrade.setText(R.string.all);

                teamClasses = null;
                teamClassPopup = null;
                selectedTeamClass = null;
                activityFilterTvTeamClass.setText(R.string.all);
            }
            selectedDistrict = districts.get(index);
            activityFilterTvArea.setText(selectedDistrict.name);
            if (selectedDistrict.id != 0) {
                DefaultParam param = new DefaultParam();
                param.put("district_id", selectedDistrict.id);
                requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_SCHOOLS, param, API_SCHOOLS);
            }
        } else if (popup.equals(schoolPopup)) {
            if (!schools.get(index).equals(selectedSchool)) {
                grades = null;
                gradePopup = null;
                selectedGrade = null;
                activityFilterTvGrade.setText(R.string.all);

                teamClasses = null;
                teamClassPopup = null;
                selectedTeamClass = null;
                activityFilterTvTeamClass.setText(R.string.all);
            }
            selectedSchool = schools.get(index);
            activityFilterTvSchool.setText(selectedSchool.name);
            if (selectedSchool.id != 0) {
                requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_GRADES,
                        new DefaultParam(), API_GRADES);
            }
        } else if (popup.equals(gradePopup)) {
            if (!grades.get(index).equals(selectedGrade)) {
                teamClasses = null;
                teamClassPopup = null;
                selectedTeamClass = null;
                activityFilterTvTeamClass.setText(R.string.all);
            }
            selectedGrade = grades.get(index);
            activityFilterTvGrade.setText(selectedGrade.name);
            if (selectedGrade.id != 0) {
                DefaultParam param = new DefaultParam();
                param.put("grade_id", selectedGrade.id);
                param.put("school_id", selectedSchool.id);
                requestTool.doGet(NetConstant.ROOT_URL + NetConstant.API_TEAM_CLASSES, param, API_TEAM_CLASSES);
            }
        } else if (popup.equals(teamClassPopup)) {
            selectedTeamClass = teamClasses.get(index);
            activityFilterTvTeamClass.setText(selectedTeamClass.name);
        }
    }

    @OnClick(R.id.activity_filter_lnCity)
    protected void openCityPop(View anchor) {
        if (cityPopup == null) {
            return;
        }
        if (cities == null || cities.size() == 0) {
            return;
        }
        cityPopup.showAsDropDown(anchor);
    }

    @OnClick(R.id.activity_filter_lnArea)
    protected void openDistrictPop(View anchor) {
        if (districtPopup == null) {
            return;
        }
        if (districts == null || districts.size() == 0) {
            return;
        }
        districtPopup.showAsDropDown(anchor);
    }

    @OnClick(R.id.activity_filter_lnSchool)
    protected void openSchoolPop(View anchor) {
        if (schoolPopup == null) {
            return;
        }
        if (schools == null || schools.size() == 0) {
            return;
        }
        schoolPopup.showAsDropDown(anchor);
    }

    @OnClick(R.id.activity_filter_lnGrade)
    protected void openGradePop(View anchor) {
        if (gradePopup == null) {
            return;
        }
        if (grades == null || grades.size() == 0) {
            return;
        }
        gradePopup.showAsDropDown(anchor);
    }

    @OnClick(R.id.activity_filter_lnTeamClass)
    protected void openClassPop(View anchor) {
        if (teamClassPopup == null) {
            return;
        }
        if (teamClasses == null || teamClasses.size() == 0) {
            return;
        }
        teamClassPopup.showAsDropDown(anchor);
    }
}
