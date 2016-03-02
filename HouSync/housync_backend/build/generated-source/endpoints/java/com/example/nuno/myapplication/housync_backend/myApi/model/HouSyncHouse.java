/*
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
/*
 * This code was generated by https://github.com/google/apis-client-generator/
 * (build: 2016-02-18 22:11:37 UTC)
 * on 2016-03-02 at 22:16:35 UTC 
 * Modify at your own risk.
 */

package com.example.nuno.myapplication.housync_backend.myApi.model;

/**
 * Model definition for HouSyncHouse.
 *
 * <p> This is the Java data model class that specifies how to parse/serialize into the JSON that is
 * transmitted over HTTP when working with the myApi. For a detailed explanation see:
 * <a href="https://developers.google.com/api-client-library/java/google-http-java-client/json">https://developers.google.com/api-client-library/java/google-http-java-client/json</a>
 * </p>
 *
 * @author Google, Inc.
 */
@SuppressWarnings("javadoc")
public final class HouSyncHouse extends com.google.api.client.json.GenericJson {

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer adminId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String createTime;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer errorCode;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.Integer houseId;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String houseName;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String lastSync;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String snapShot;

  /**
   * The value may be {@code null}.
   */
  @com.google.api.client.util.Key
  private java.lang.String snapShotUser;

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getAdminId() {
    return adminId;
  }

  /**
   * @param adminId adminId or {@code null} for none
   */
  public HouSyncHouse setAdminId(java.lang.Integer adminId) {
    this.adminId = adminId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getCreateTime() {
    return createTime;
  }

  /**
   * @param createTime createTime or {@code null} for none
   */
  public HouSyncHouse setCreateTime(java.lang.String createTime) {
    this.createTime = createTime;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getErrorCode() {
    return errorCode;
  }

  /**
   * @param errorCode errorCode or {@code null} for none
   */
  public HouSyncHouse setErrorCode(java.lang.Integer errorCode) {
    this.errorCode = errorCode;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.Integer getHouseId() {
    return houseId;
  }

  /**
   * @param houseId houseId or {@code null} for none
   */
  public HouSyncHouse setHouseId(java.lang.Integer houseId) {
    this.houseId = houseId;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getHouseName() {
    return houseName;
  }

  /**
   * @param houseName houseName or {@code null} for none
   */
  public HouSyncHouse setHouseName(java.lang.String houseName) {
    this.houseName = houseName;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getLastSync() {
    return lastSync;
  }

  /**
   * @param lastSync lastSync or {@code null} for none
   */
  public HouSyncHouse setLastSync(java.lang.String lastSync) {
    this.lastSync = lastSync;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSnapShot() {
    return snapShot;
  }

  /**
   * @param snapShot snapShot or {@code null} for none
   */
  public HouSyncHouse setSnapShot(java.lang.String snapShot) {
    this.snapShot = snapShot;
    return this;
  }

  /**
   * @return value or {@code null} for none
   */
  public java.lang.String getSnapShotUser() {
    return snapShotUser;
  }

  /**
   * @param snapShotUser snapShotUser or {@code null} for none
   */
  public HouSyncHouse setSnapShotUser(java.lang.String snapShotUser) {
    this.snapShotUser = snapShotUser;
    return this;
  }

  @Override
  public HouSyncHouse set(String fieldName, Object value) {
    return (HouSyncHouse) super.set(fieldName, value);
  }

  @Override
  public HouSyncHouse clone() {
    return (HouSyncHouse) super.clone();
  }

}
